import React, {PropTypes} from 'react';
import { Components } from '@opuscapita/service-base-ui';

// import React from 'react';
// const PropTypes = import('react');
// const Components = import('@opuscapita/service-base-ui');

class Button extends Components.ContextComponent {

    static propTypes = {
        onClick: PropTypes.func.isRequired,
        disabled: PropTypes.bool,
        hidden: PropTypes.bool,
        className: PropTypes.string,
        type: PropTypes.string,
        label: PropTypes.string
    };

    static defaultProps = {
        onClick: () => null,
        disabled: false,
        hidden: false,
        className: 'btn btn-default',
        type: 'button',
        label: ''
    };


    state = {};

    constructor(props) {
        super(props);
        this.state = {
            ...props
        };
    }

    render() {
        return (
            !this.state.hidden && <button
                type={this.state.type}
                className={this.state.className}
                disabled={this.state.disabled}
                onClick={() => this.state.onClick()}
            >
                {this.state.label}
            </button>
        );
    }
}
export default Button;
