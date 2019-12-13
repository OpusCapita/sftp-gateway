import React, {PropTypes} from 'react';
import {Components} from '@opuscapita/service-base-ui';


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

    constructor(props, context) {
        super(props, context);
        this.state = {
            ...props,
            ...context
        };
    }

    componentWillReceiveProps = (newProps) => {
        this.setState({
            ...newProps
        });
    };

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
