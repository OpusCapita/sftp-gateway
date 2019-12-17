import React, {PropTypes} from 'react';

import {Components} from '@opuscapita/service-base-ui';
import {FormInput, Select} from '../component';
import './style.css';

class DialogForm extends Components.ContextComponent {
    state = {};
    static propTypes = {
        data: PropTypes.object,
        actions: PropTypes.array
    };

    constructor(props, context) {
        super(props, context);
        this.state = {
            ...props,
            ...context,
            errors: []
        };
    }

    handleSelect = (event) => {
        const target = event.target;
        const value = target.value;
        const _row = this.state.data;
        let _errors = {};
        _row['action'] = value;
        if (value === '') {
            _errors['action'] = "Please select a valid action";
        } else {
            _row['actionName'] = this.state.actions.find(action => action.key === value).value;
        }
        this.setState({
            data: _row,
            error: _errors
        });
    };

    handleChange = (event) => {

        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        const _row = this.state.data;
        _row[name] = value;

        this.setState({
            data: _row
        });
    };

    handleValidation = () => {

        const {i18n} = this.context;

        const _data = this.state.data;
        let _errors = {
            "name": [],
            "description": [],
            "fileFilter": [],
            "path": [],
            "action": []
        };
        let _formIsValid = true;
        let _key = 0;

        //Name
        if (_data["name"].length < 1) {
            _formIsValid = false;
            _errors["name"][_key] = i18n.getMessage('gateway.sftp.notification.field.empty');
            _key++;
        }

        //FileFilter
        if (_data["fileFilter"].length < 1) {
            _formIsValid = false;
            _errors["fileFilter"][_key] = i18n.getMessage('gateway.sftp.notification.field.empty');
            _key++;
        }

        //Action
        if (_data["action"] === '') {
            _formIsValid = false;
            _errors["action"][_key] = i18n.getMessage('gateway.sftp.notification.valid');
            _key++;
        }

        //Path
        if (!_data["path"].length > 0) {
            _formIsValid = false;
            _errors["path"][_key] = i18n.getMessage('gateway.sftp.notification.field.empty');
        } else if (typeof _data["path"] !== "undefined") {
            const backslash = '/';
            const firstChar = _data["path"].charAt(0);
            const lastChar = _data["path"].charAt(_data["path"].length - 1);

            if (firstChar !== backslash) {
                _formIsValid = false;
                _errors["path"][_key] = i18n.getMessage('gateway.sftp.notification.field.path.begin');
                _key++;
            }
            if (lastChar !== backslash) {
                _formIsValid = false;
                _errors["path"][_key] = i18n.getMessage('gateway.sftp.notification.field.path.end');
            }
        }

        this.setState({
            errors: _errors
        });
        return _formIsValid;
    };

    render() {
        const {i18n} = this.context;

        return (
            <div className="form-horizontal">
                <FormInput label={i18n.getMessage('gateway.sftp.name')} errors={this.state.errors["name"]}>
                    <input type="text"
                           className="form-control"
                           id="profile-name"
                           name="name"
                           value={this.state.data.name}
                           onChange={this.handleChange}
                           required={true}
                    />
                </FormInput>
                <FormInput label={i18n.getMessage('gateway.sftp.description')}
                           errors={this.state.errors["description"]}>
                    <textarea className="form-control"
                              id="profile-description"
                              name="description"
                              onChange={this.handleChange}
                              defaultValue={this.state.data.description}
                    />
                </FormInput>
                <FormInput label={i18n.getMessage('gateway.sftp.path')} errors={this.state.errors["path"]}>
                    <input type="text"
                           className="form-control"
                           id="profile-path"
                           name="path"
                           value={this.state.data.path}
                           onChange={this.handleChange}
                           required={true}
                    />
                </FormInput>
                <FormInput label={i18n.getMessage('gateway.sftp.fileFilter')} errors={this.state.errors["fileFilter"]}>
                    <input type="text"
                           className="form-control"
                           id="profile-filefilter"
                           name="fileFilter"
                           value={this.state.data.fileFilter}
                           onChange={this.handleChange}
                           required={true}
                    />
                </FormInput>
                <FormInput label={i18n.getMessage('gateway.sftp.action')} errors={this.state.errors["action"]}>
                    <Select
                        className="form-control"
                        id="profile-action"
                        name="action"
                        required={true}
                        options={this.state.actions}
                        value={this.state.data.action}
                        onChange={this.handleSelect.bind(this)}
                    />
                </FormInput>
            </div>
        );
    }
}

export default DialogForm;