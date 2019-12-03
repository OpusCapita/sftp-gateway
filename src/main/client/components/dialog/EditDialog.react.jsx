import React from "react";

import {Components} from '@opuscapita/service-base-ui';
import {Button, Select} from '../component';
import './style.css';

class EditDialog extends Components.ContextComponent {
    state = {};

    get error() {
        let _error = false;
        if (this.state.data.name === "" ||
            this.state.data.path === "") {
            _error = true;
        }
        return _error;
    }

    constructor(props) {
        super(props);
        this.state = {
            ...props,
            errors: []
        };
    }

    handleSelect = (event) => {
        console.log('handleSelect', event);

        const target = event.target;
        const value = target.value;
        const _row = this.state.data;
        _row['action'] = value;
        _row['actionName'] = this.state.actions.find(action => action.key === value).value;
        this.setState({
            data: _row,
            error: this.error
        });
    };

    handleChange = (event) => {
        console.log('handleChange', event);

        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        const _row = this.state.data;
        _row[name] = value;
        this.setState({
            data: _row,
            error: this.error
        });
    };

    handleSubmit = () => {
        if (this.handleValidation()) {
            this.state.onSubmit(this.state.data);
        }
    };

    handleEdit = () => {
        if (this.handleValidation()) {
            this.state.onEdit(this.state.data);
        }
    };

    handleValidation = () => {
        const _data = this.state.data;
        let _errors = {};
        let _formIsValid = true;

        //Name
        if (_data["name"].length < 1) {
            _formIsValid = false;
            _errors["name"] = "Cannot be empty";
        }

        //FileFilter
        if (_data["fileFilter"].length < 1) {
            _formIsValid = false;
            _errors["fileFilter"] = "Cannot be empty";
        }

        // if(typeof _data["name"] !== "undefined"){
        //     if(!_data["name"].match(/^[a-zA-Z0-9]+$/)){
        //         _formIsValid = false;
        //         _errors["name"] = "Only letters";
        //     }
        // }

        //Path
        if (!_data["path"].length > 0) {
            _formIsValid = false;
            _errors["path"] = "Cannot be empty";
        } else if (typeof _data["path"] !== "undefined") {
            const backslash = '/';
            const firstChar = _data["path"].charAt(0);
            const lastChar = _data["path"].charAt(_data["path"].length - 1);

            if (firstChar !== backslash) {
                _formIsValid = false;
                _errors["path"] = "Path is not valid. Should begin with \"" + backslash + "\"";
            }
            if (lastChar !== backslash) {
                _formIsValid = false;
                _errors["path"] = "Path is not valid. Should end with \"" + backslash + "\"";
            }
        }

        this.setState({errors: _errors});
        return _formIsValid;
    };

    handleCancel = () => {
        this.state.onCancel();
    };

    render() {
        console.log('render()', this.state);
        return (
            <div className='overlay'>
                <div className="modal-dialog overlay-content" role="dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close"
                                    onClick={this.handleCancel}><span
                                aria-hidden="true">&times;</span></button>
                            <h4 className="modal-title" id="exampleModalLabel">{this.state.title}</h4>
                        </div>
                        <div className="modal-body">
                            <form>
                                <div className="form-group">
                                    <label htmlFor="profile-name" className="control-label">Name:</label>
                                    <input type="text" className="form-control" id="profile-name" name="name"
                                           value={this.state.data.name}
                                           onChange={this.handleChange} required={true}/>
                                    <span style={{color: "red"}}>{this.state.errors["name"]}</span>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="profile-description" className="control-label">Description:</label>
                                    <textarea className="form-control" id="profile-description" name="description"
                                              onChange={this.handleChange} defaultValue={this.state.data.description}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="profile-path" className="control-label">Path:</label>
                                    <input type="text" className="form-control" id="profile-path" name="path"
                                           value={this.state.data.path}
                                           onChange={this.handleChange} required={true}/>
                                    <span style={{color: "red"}}>{this.state.errors["path"]}</span>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="profile-filefilter" className="control-label">File Filter:</label>
                                    <input type="text" className="form-control" id="profile-filefilter"
                                           name="fileFilter"
                                           value={this.state.data.fileFilter}
                                           onChange={this.handleChange} required={true}/>
                                    <span style={{color: "red"}}>{this.state.errors["fileFilter"]}</span>
                                </div>
                                <div className="form-group">
                                    <Select
                                        className="form-control"
                                        id="profile-action"
                                        name="action"
                                        required={true}
                                        options={this.state.actions}
                                        value={this.state.data.action}
                                        onChange={this.handleSelect.bind(this)}
                                    />
                                    <span style={{color: "red"}}>{this.state.errors["action"]}</span>
                                </div>
                            </form>
                        </div>
                        <div className="modal-footer">
                            <Button type="button"
                                    className="btn btn-default"
                                    data-dismiss="modal"
                                    onClick={() => this.handleCancel()}
                                    label="Close"
                            />
                            <Button
                                type="submit"
                                className="btn btn-primary"
                                onClick={() => this.handleSubmit()}
                                label="Create"
                                hidden={this.state.edit}
                            />
                            <Button
                                type="submit"
                                className="btn btn-primary"
                                onClick={() => this.handleEdit()}
                                label="Edit"
                                hidden={!this.state.edit}
                            />
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default EditDialog;