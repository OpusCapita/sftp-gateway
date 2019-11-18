import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';

export default class Button extends Components.ContextComponent {

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
        super();
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
