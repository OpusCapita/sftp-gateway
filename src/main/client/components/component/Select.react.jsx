import React, {PropTypes} from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {Option} from '../component';

// import React from 'react';
// const PropTypes = import('react');
// const Components = import('@opuscapita/service-base-ui');
// const Option = import('../component');

class Select extends Components.ContextComponent {

    static propTypes = {
        id: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        onChange: PropTypes.func.isRequired,
        onSelect: PropTypes.func.isRequired,
        disabled: PropTypes.bool,
        hidden: PropTypes.bool,
        required: PropTypes.bool,
        className: PropTypes.string,
        label: PropTypes.string,
        value: PropTypes.any,
        options: PropTypes.array.isRequired
    };

    static defaultProps = {
        id: '',
        name: '',
        onChange: () => null,
        onSelect: () => null,
        options: [],
        value: null,
        disabled: false,
        hidden: false,
        required: false,
        className: 'form-control"',
        label: '',
    };

    state = {};

    constructor(props) {
        super(props);
        this.state = {
            ...props
        };
    }

    render() {
        console.log('renderSelect', this.state);
        return (
            !this.state.hidden &&
            <div>
                <label htmlFor={this.state.id} className="control-label">Action:</label>
                <select className={this.state.className}
                        id={this.state.id}
                        name={this.state.name}
                        required={this.required}
                        onChange={(e) => this.state.onChange(e)}
                        onSelect={(e) => this.state.onSelect(e)}
                        value={this.state.value}>
                    <Option
                        key=''
                        value=''
                        label='None'
                        disabled={true}
                    />
                    {this.state.options.map((object) => {
                        return (<Option
                            key={object.key}
                            value={object.key}
                            label={object.value}
                            selected={object.key === this.state.value}
                        />)
                    })}
                </select>
            </div>
        );
    };
}

export default Select;