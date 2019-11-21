import React, {PropTypes} from 'react';
import { Components } from '@opuscapita/service-base-ui';

// import React from 'react';
// const PropTypes = import('react');
// const Components = import('@opuscapita/service-base-ui');

class Option extends Components.ContextComponent {

    static propTypes = {
        label: PropTypes.string.isRequired,
        value: PropTypes.any.isRequired,
        selected: PropTypes.bool
    };

    static defaultProps = {
        label: '',
        value: '',
        selected: false
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
            <option selected={this.state.select} value={this.state.value}>{this.state.label}</option>
        );
    };
}
export default Option;