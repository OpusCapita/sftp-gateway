import React, {PropTypes} from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {Option} from '../component';

class Select extends Components.ContextComponent {

    static propTypes = {
        id: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        onChange: PropTypes.func.isRequired,
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
        options: [],
        value: null,
        disabled: false,
        hidden: false,
        required: false,
        className: 'form-control"',
        label: '',
    };

    state = {};

    constructor(props, context) {
        super(props, context);
        this.state = {
            ...props,
            ...context
        };
    }

    select = (event) => {
        this.setState({
            value: event.target.value
        });
        this.state.onChange(event);
    };

    render() {
        return (
            !this.state.hidden &&
            <div>
                <select className={this.state.className}
                        id={this.state.id}
                        name={this.state.name}
                        required={this.required}
                        onChange={(e) => this.select(e)}
                        value={this.state.value}>
                    <Option
                        key=''
                        value=''
                        label='None'
                        disabled={true}
                        selected={this.state.value === null}
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