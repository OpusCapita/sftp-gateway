import React from 'react';
import {SFTPConfigurator} from "./components/configurator";
// const SFTPConfigurator = import('./components/configurator');

const App = () => (
    <SFTPConfigurator businessPartnerId='OC001' serviceProfileId='SP_001_DE'/>
);
export default App;