import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ConfigDataGrid} from "../datagridcomponent"
import RequestApi from "../helper/RequestApi";
import EditDialog from "../dialog/EditDialog.react";

// const ConfigDataGrid = import('../datagridcomponent');
// const RequestApi = import('../helper/RequestApi');
// const {OCAlertsProvider, OCAlert} = import('@opuscapita/react-alerts')
// const SimpleModal = import('@opuscapita/react-overlays');
// const EditDialog = import('../dialog');
// const Components = import('@opuscapita/service-base-ui');

class SFTPConfigurator extends Components.ContextComponent {
    request = new RequestApi();
    state = {};
    columns = [
        {
            key: 'position',
            name: 'ID',
            sortDescendingFirst: true,
            sortable: true
        },
        {
            key: 'id',
            name: '',
            sortDescendingFirst: true,
            sortable: true,
            visible: false
        },
        {
            key: 'businessPartnerId',
            name: 'Business partner ID',
            sortable: true,
            visible: false
        },
        {
            key: 'serviceProfileId',
            name: 'Service profile ID',
            sortable: true,
            visible: false
        },
        {
            key: 'name',
            name: 'Name',
            sortable: true
        },
        {
            key: 'description',
            name: 'Description',
            sortable: false
        },
        {
            key: 'path',
            name: 'Path',
            sortable: true
        },
        {
            key: 'fileFilter',
            name: 'File Filter',
            sortable: false
        },
        {
            key: 'actionName',
            name: 'Action',
            sortable: false,
            visible: true
        }
    ];

    constructor(props, context) {
        super(props);
        console.log('props', props);
        console.log('context', context);
        this.loadData();
        this.state = {
            ...props,
            ...context,
            loadingState: false,
            endState: false,
            showModal: false,
            rows: [],
            toEdit: null,
            actions: []
        };
        console.log('state', this.state);
    };

    loadData = () => {
        this.request.checkBackendAvailability().then(() => {
            this.request.getEventActions().then((_data) => {
                let _actions = [];
                _data.forEach((action) => {
                    _actions.push({
                        key: Object.keys(action)[0],
                        value: Object.values(action)[0],
                    });
                });
                this.setState({
                    actions: _actions
                });
            }).catch((error) => {
                this.state.showNotification('Backend is not available', 'error', 4);
            });
            this.request.getServiceConfigurations().then((data) => {
                this.setState({rows: data});
            }).catch((error) => {
                this.state.showNotification('Backend is not available', 'error', 4);
            });
        }).catch(() => {
            this.state.showNotification('Backend is not available', 'error', 4);
        });
    };

    save = (rows) => {
        this.request.saveServiceConfigurations(rows).then((_rows) => {
            this.setState({rows: _rows});
            this.state.showNotification('Operation was successful!', 'success', 4);
        }).catch((error) => {
            this.state.showNotification('Operation was not successful!', 'warning', 4);
        });
    };

    delete = (rows) => {
        this.request.deleteServiceConfigurations(rows).then((_rows) => {
        });
    };

    editRow = (row) => {
        const clone = JSON.parse(JSON.stringify(row));
        this.setState({
            toEdit: clone,
            showModal: true,
            edit: true
        });
    };

    edit = (row) => {
        this.state.rows[row.position - 1] = row;
        this.setState({
            showModal: false,
            edit: false
        });
    };

    addRow = () => {
        this.setState({
            toEdit: this.createEmptyRow(),
            showModal: true,
            edit: false
        });
    };

    createEmptyRow = () => {
        return {
            id: null,
            businessPartnerId: this.props.businessPartnerId,
            serviceProfileId: this.props.serviceProfileId,
            name: "",
            description: "",
            path: "",
            fileFilter: "",
            action: "",
            deleted: false
        }
    };

    add = (row) => {
        const _rows = Array.from(this.state.rows);
        _rows.push(row);
        this.setState({
            rows: _rows,
            showModal: false,
            edit: false
        });
    };

    cancel = () => {
        this.setState({
            showModal: false,
            edit: false
        });
    };

    render() {
        return (
            <div className='row'>
                <div
                    style={{
                        padding: '24px',
                        backgroundColor: 'transparent',
                        overflow: 'hidden',
                        display: 'inline-block',
                        verticalAlign: 'middle',
                        position: 'absolute'
                    }}
                >
                    {
                        this.state.showModal &&
                        this.state.toEdit !== null &&
                        <EditDialog
                            data={this.state.toEdit}
                            onSubmit={this.add.bind(this)}
                            onEdit={this.edit.bind(this)}
                            onCancel={this.cancel.bind(this)}
                            actions={this.state.actions}
                            edit={this.state.edit}
                        />
                    }
                </div>
                <ConfigDataGrid
                    columns={this.columns}
                    rows={this.state.rows}
                    onSave={this.save.bind(this)}
                    onDelete={this.delete.bind(this)}
                    onEdit={this.editRow.bind(this)}
                    onAddRow={this.addRow.bind(this)}
                    onReload={this.loadData.bind(this)}
                />
            </div>
        );
    };
}

export default SFTPConfigurator;