import React from 'react';
import {Components} from '@opuscapita/service-base-ui';

class SFTPConfigurator extends Components.ContextComponent {
    state = {
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const page = this.context.router.location.query.r;

        if (page) {
            this.showPage(page)
        }
    }

    showPage(page, event) {
        event && event.preventDefault();
        this.context.router.push(`/sftp-gateway/${page}`);
    }

    render() {
        return (<div>
            <h2>SFTP Config</h2>
        </div>);
    };
}

export default SFTPConfigurator;