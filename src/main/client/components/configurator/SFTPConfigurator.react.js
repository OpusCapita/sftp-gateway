import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import ConfigDataGrid from "../datagridcomponent"
import RequestApi from "../helper/RequestApi";
import {OCAlertsProvider} from '@opuscapita/react-alerts';
import {SimpleModal} from "@opuscapita/react-overlays";
import EditDialog from "../dialog";


export default class SFTPConfigurator extends Components.ContextComponent {
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
            sortable: true
        },
        {
            key: 'path',
            name: 'Path',
            sortable: true
        }
    ];

    constructor(props, context) {
        super(props);
        this.loadData();
        this.state = {
            loadingState: false,
            endState: false,
            showModal: false,
            rows: [],
            toEdit: null
        };
    };

    loadData = () => {
        this.request.getServiceConfigurations().then((data) => {
            this.setState({rows: data});
        });
    };

    save = (rows) => {
        this.request.saveServiceConfigurations(rows).then((_rows) => {
            this.setState({rows: _rows});
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
            <div className='row' style={{
                overflow: 'hidden'
            }}>
                <ConfigDataGrid
                    columns={this.columns}
                    rows={this.state.rows}
                    onSave={this.save.bind(this)}
                    onDelete={this.delete.bind(this)}
                    onEdit={this.editRow.bind(this)}
                    onAddRow={this.addRow.bind(this)}
                    onReload={this.loadData.bind(this)}
                />
                <OCAlertsProvider/>

                <SimpleModal
                    isShow={this.state.showModal}
                    style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}
                >
                    <div
                        style={{padding: '24px', backgroundColor: 'transparent'}}
                    >
                        {this.state.showModal && this.state.toEdit !== null && <EditDialog data={this.state.toEdit}
                                                                                           onSubmit={this.add.bind(this)}
                                                                                           onEdit={this.edit.bind(this)}
                                                                                           onCancel={this.cancel.bind(this)}
                                                                                           edit={this.state.edit}
                        />}
                    </div>
                </SimpleModal>
            </div>
        );
    };
}
