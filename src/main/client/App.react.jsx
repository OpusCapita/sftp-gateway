import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import {SFTPConfigurator} from "./components/configurator";
import {Route} from 'react-router';

const configurator = () => {
    return (
        <div>
            <SFTPConfigurator
                businessPartnerId='OC001'
                serviceProfileId='SP_001_DE'
            />
        </div>
    );
};

const App = () => (
    <Containers.ServiceLayout serviceName="sftp-gateway">
        <Route path="/" component={configurator}/>
    </Containers.ServiceLayout>
);
export default App;