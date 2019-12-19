import React, {PropTypes} from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ConfigDataGrid} from "../datagridcomponent"
import RequestApi from "../helper/RequestApi";
import DialogForm from "../sftpform/SftpConfigForm.react";
import i18nMessages from '../i18n';

class SFTPConfigurator extends Components.ContextComponent {

    static propTypes = {
        businessPartnerId: PropTypes.string.isRequired,
        serviceProfileId: PropTypes.string.isRequired,
        visible: PropTypes.boolean
    };

    static defaultProps = {
        visible: true
    };

    request = new RequestApi();
    state = {};
    columns = [];


    constructor(props, context) {
        super(props, context);
        context.i18n.register('SftpGateway', i18nMessages);

        this.loadData();
        this.state = {
            ...context,
            ...props,
            loadingState: false,
            endState: false,
            showModal: false,
            rows: [],
            toEdit: null,
            actions: []
        };
        this.columns = [
            {
                key: 'position',
                name: '',
                sortDescendingFirst: true,
                sortable: true,
                visible: false
            },
            {
                key: 'id',
                name: context.i18n.getMessage('gateway.sftp.id'),
                sortDescendingFirst: true,
                sortable: true,
                visible: false
            },
            {
                key: 'businessPartnerId',
                name: context.i18n.getMessage('gateway.sftp.businessPartnerId'),
                sortable: true,
                visible: false
            },
            {
                key: 'serviceProfileId',
                name: context.i18n.getMessage('gateway.sftp.serviceProfileId'),
                sortable: true,
                visible: false
            },
            {
                key: 'name',
                name: context.i18n.getMessage('gateway.sftp.name'),
                sortable: true
            },
            {
                key: 'description',
                name: context.i18n.getMessage('gateway.sftp.description'),
                sortable: false
            },
            {
                key: 'path',
                name: context.i18n.getMessage('gateway.sftp.path'),
                sortable: true
            },
            {
                key: 'fileFilter',
                name: context.i18n.getMessage('gateway.sftp.fileFilter'),
                sortable: false
            },
            {
                key: 'actionName',
                name: context.i18n.getMessage('gateway.sftp.action'),
                sortable: false,
                visible: true
            }
        ];
    };

    deleteByServiceProfileId = async () => {
        return this.request.deleteByServiceProfileId(this.props.businessPartnerId, this.props.serviceProfileId);
    };

    deleteByBusinessPartnerId = async () => {
        return this.request.deleteByBusinessPartnerId(this.props.businessPartnerId);
    };

    loadData = async () => {
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
                this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.backend_not_available'), 'error', 4);
            });
            this.request.getServiceConfigurations(this.props.businessPartnerId, this.props.serviceProfileId).then((data) => {
                this.setState({rows: data});
            }).catch((error) => {
                this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.backend_not_available'), 'error', 4);
            });
        }).catch((error) => {
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.backend_not_available'), 'error', 4);
        });
    };

    save = (row) => {
        this.request.editServiceConfiguration(row).then((_rows) => {
            this.setState({
                rows: _rows,
                toEdit: null,
                showModal: false,
                edit: false
            });
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.success'), 'success', 4);
        }).catch((error) => {
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.warning'), 'warning', 4);
        });
    };

    create = (row) => {
        this.request.createServiceConfiguration(row).then((_rows) => {
            this.setState({
                rows: _rows,
                toEdit: null,
                showModal: false,
                edit: false
            });
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.success'), 'success', 4);
        }).catch((error) => {
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.warning'), 'warning', 4);
        });
    };


    delete = (row) => {
        this.request.deleteServiceConfiguration(row).then((_rows) => {
            this.setState({rows: _rows});
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.success'), 'success', 4);
        }).catch((error) => {
            this.context.showNotification(this.context.i18n.getMessage('gateway.sftp.notification.warning'), 'warning', 4);
        });
    };

    edit = (row) => {
        const clone = JSON.parse(JSON.stringify(row));
        this.setState({
            toEdit: clone,
            showModal: true,
            edit: true
        });
    };

    add = () => {
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
            actionName: "",
            deleted: false
        }
    };

    cancel = () => {
        this.setState({
            toEdit: null,
            showModal: false,
            edit: false
        });
    };

    render() {
        const {i18n} = this.context;
        const modalTitle = this.state.edit ? i18n.getMessage('gateway.sftp.modal.edit') + ': ' + this.state.toEdit.id : i18n.getMessage('gateway.sftp.modal.new');
        return (
            this.props.visible && <div>
                {
                    this.state.toEdit !== null &&
                    <Components.ModalDialog
                        ref={ref => this.modalDialog = ref}
                        buttons={{
                            'save': i18n.getMessage('gateway.sftp.button.save'),
                            'cancel': i18n.getMessage('gateway.sftp.button.cancel')
                        }}
                        size='large'
                        visible={this.state.showModal}
                        allowClose={false}
                        title={modalTitle}
                        onButtonClick={(cmd) => {
                            if (cmd === 'save') {
                                if (this.dialogForm.handleValidation()) {
                                    if (this.state.edit) {
                                        this.save(this.state.toEdit);
                                    } else {
                                        this.create(this.state.toEdit);
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }}
                        onClose={() => this.cancel()}
                    >
                        <DialogForm
                            data={this.state.toEdit}
                            actions={this.state.actions}
                            ref={ref => this.dialogForm = ref}
                        />
                    </Components.ModalDialog>
                }
                <ConfigDataGrid
                    columns={this.columns}
                    rows={this.state.rows}
                    onSave={this.save.bind(this)}
                    onDelete={this.delete.bind(this)}
                    onEdit={this.edit.bind(this)}
                    onAddRow={this.add.bind(this)}
                    onReload={this.loadData.bind(this)}
                />
            </div>
        );
    };
}

export default SFTPConfigurator;