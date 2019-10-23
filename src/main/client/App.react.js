import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import {Route} from 'react-router';
import {SFTPConfigurator} from './components/configurator/SFTPConfigurator.react';

const menu = (router) => (
    <div className="nav">
        <h1>SFTP- Gateway</h1>
    </div>
);

const home = (props) => (
    <div>
        {menu(props.router)}
        <SFTPConfigurator/>
    </div>
);

const App = () => (
    <div>
        {menu(props.router)}
        <SFTPConfigurator/>
    </div>
    // <Containers.ServiceLayout serviceName="sftp-gateway">
    //     <Route path="/" component={home}/>
    // </Containers.ServiceLayout>
);
export default App;