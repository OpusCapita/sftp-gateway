import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';

export default class Option extends Components.ContextComponent {

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
        super();
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
