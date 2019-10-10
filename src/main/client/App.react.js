import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import {Route} from 'react-router';

// const home = (props) => ();

const App = () => (
    <Containers.ServiceLayout serviceName="sftp-gateway">
        <Route path="/" component={"/"}/>
    </Containers.ServiceLayout>
);
export default App;