import request from 'superagent';
// const request = import('superagent');

export default class RequestApi {
    _request = request;

    _serviceUrl = './api';
    _serviceUrlDev = 'http://localhost:2223/api';

    async getServiceConfigurations() {
        return await this._request.get(this._serviceUrl + '/sftp/').then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async saveServiceConfigurations(data) {
        return await this._request.post(this._serviceUrl + '/sftp/').send(data).then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async deleteServiceConfigurations(data) {
        return await this._request.delete(this._serviceUrl + '/sftp/').send(data).then((response) => {
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

    async checkBackendAvailability() {
        return this._request.get(this._serviceUrl + '/health/check');
    }
}