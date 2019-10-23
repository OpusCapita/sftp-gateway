import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {tenants} from "../helper/helper.service"
import {ConfigDataGrid} from "../datagridcomponent/ConfigDataGrid.react"

// import i18nMessages from './i18n';

export class SFTPConfigurator extends Components.ContextComponent {
    state = {};
    columns = [
        {
            key: 'id',
            name: '',
            sortDescendingFirst: true,
            sortable: true,
            editable: false
        },
        {
            key: 'businessPartnerId',
            name: 'Business partner ID',
            sortable: true,
            editable: false
        },
        {
            key: 'serviceProfileId',
            name: 'Service profile ID',
            sortable: true,
            editable: true
        },
        {
            key: 'name',
            name: 'Name',
            sortable: true,
            editable: true
        },
        {
            key: 'description',
            name: 'Description',
            sortable: true,
            editable: true
        },
        {
            key: 'path',
            name: 'Path',
            sortable: true,
            editable: true
        }
    ];

    rows = [];

    constructor(props, context) {
        super(props);
        let _tenants = tenants;
        _tenants.forEach(function (object) {
            object.label = object.name;
            object.value = object.id;
            object.active = false;
        });
        this.state = {
            selectableTenants: _tenants,
            currentTenant: null,
            loadingState: false,
            endState: false,
            showModal: false
        }
    };

    save = (rows) => {
        this.rows = rows;
    };

    addRow = (idx) => {
        return {
            id: idx,
            businessPartnerId: "OC001",
            serviceProfileId: "ServiceProfileID_idx_" + idx,
            name: "",
            description: "",
            path: ""
        }
    };

    render() {
        return (<div className='col-xs-12 col-sm-offset-1 col-sm-10'>
            <h2>Config</h2>
            <div className='row'>
                <ConfigDataGrid
                    columns={this.columns}
                    rows={this.rows}
                    onSave={this.save.bind(this)}
                    onAddRow={this.addRow.bind(this)}
                />
            </div>
        </div>);
    };
}
