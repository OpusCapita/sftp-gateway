import request from 'superagent';

export default class RequestApi {
    _request = request;

    _serviceUrl = '/sftp-gateway/api';
    _serviceUrlDev = 'http://localhost:2223/api';

    async getServiceConfigurations() {
        return await this._request.get(this._serviceUrl + '/').then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async saveServiceConfigurations(data) {
        return await this._request.post(this._serviceUrl + '/').send(data).then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async deleteServiceConfigurations(data) {
        return await this._request.delete(this._serviceUrl + '/').send(data).then((response) => {
            console.log('DELETE', response.body);
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async getEventActions() {
        return await this._request.get(this._serviceUrl + '/evnt/').then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }
}