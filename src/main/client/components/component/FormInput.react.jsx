import React, {PropTypes} from 'react';
import {Components} from '@opuscapita/service-base-ui';

class FormInput extends Components.ContextComponent {

    static propTypes = {
        label: PropTypes.string.isRequired,
        required: PropTypes.bool,
        errors: PropTypes.array,
        template: PropTypes.bool,
        inputContainerClassName: PropTypes.string,
    };

    static defaultProps = {
        required: false,
        errors: [],
        template: false,
        inputContainerClassName: '',
    };

    static errorStyles() {
        return {
            marginBottom: '0px',
            padding: '6px',
            border: '0px'
        };
    }

    render() {
        const { required, errors, label, children } = this.props;

        return (
            <div className={`form-group ${errors.length ? 'has-error' : ''}`}>
                <label className={`col-sm-4 control-label`}>
                    {label}{required && '\u00a0*'}
                </label>

                <div className={`col-sm-8 ${this.props.inputContainerClassName}`}>
                    {children}

                    {errors.map((message, index) =>
                        <div className="alert alert-danger" key={index} style={FormInput.errorStyles()}>
                            <span>{ message }</span>
                        </div>)}
                </div>
            </div>
        );
    }
}

export default FormInput;