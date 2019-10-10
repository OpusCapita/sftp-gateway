import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import {Route} from 'react-router';
import {SFTPConfigurator} from 'components/configurator/SFTPConfigurator.react';

const home = () => (
    <SFTPConfigurator/>
);

const App = () => (
    <Containers.ServiceLayout serviceName="sftp-gateway">
        <Route path="/" component={home}/>
    </Containers.ServiceLayout>
);
export default App;