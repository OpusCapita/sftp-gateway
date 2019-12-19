import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {Button} from '../component';

class ConfigDataGrid extends Components.ContextComponent {

    state = {};
    columns = [];

    constructor(props, context) {
        super(props, context);

        this.columns = props.columns
            .filter(column => column.visible !== false);
        this.state = {
            ...props,
            ...context,
            edited: false
        };
    };

    componentWillReceiveProps = (newProps) => {
        this.setState({
            ...newProps
        });
    };

    EmptyRowsView = (props) => {
        return (
            <div style={{
                textAlign: "center",
                backgroundColor: "transparent",
                padding: "20px"
            }}>
                <h3>{props.message}</h3>
            </div>
        );
    };

    /**
     * Context Menu Actions
     */
    onItemClick = (key, row) => {
        if (key === 'edit') {
            this.state.onEdit(row);
        } else if (key === 'delete') {
            this.state.onDelete(row);
        }
    };

    render() {
        const {i18n} = this.context;
        const {rows} = this.props;

        return (
            <div>
                <div className="btn-toolbar" role="toolbar">
                    <div className="btn-group"
                         role="group"
                         style={{display: 'inline'}}>
                        <Button type="button"
                                className="btn btn-primary"
                                onClick={() => this.state.onAddRow()}
                                label={i18n.getMessage('gateway.sftp.button.add')}
                        />
                    </div>
                </div>
                {rows.length > 0 && <Components.ListTable
                    columns={this.columns}
                    items={rows}
                    onButtonClick={this.onItemClick}
                    itemButtons={
                        [
                            {
                                key: 'edit',
                                label: i18n.getMessage('gateway.sftp.button.edit'),
                                icon: 'edit'
                            },
                            {
                                key: 'delete',
                                label: i18n.getMessage('gateway.sftp.button.delete'),
                                icon: 'trash'
                            }
                        ]
                    }
                />}
                {
                    rows.length === 0 && <this.EmptyRowsView message={i18n.getMessage('gateway.sftp.no_data')}/>
                }
            </div>
        );
    }
}

export default ConfigDataGrid;