import request from 'superagent';

export default class RequestApi {
    _request = request;

    _serviceUrl = '/sftp-gateway/api';

    _configuration = '/configs';
    _events = '/evnts';

    async getServiceConfigurations(businessPartnerId, serviceProfileId) {
        return await this._request.get(
            this._serviceUrl
            + this._configuration + '/'
            + businessPartnerId + '/'
            + serviceProfileId
        ).then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async createServiceConfiguration(data) {
        return await this._request.post(this._serviceUrl + this._configuration + '/').send(data).then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async editServiceConfiguration(data) {
        return await this._request.put(this._serviceUrl + this._configuration + '/').send(data).then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async deleteServiceConfiguration(data) {
        return await this._request.delete(
            this._serviceUrl
            + this._configuration + '/'
            + data.businessPartnerId + '/'
            + data.serviceProfileId + '/'
            + data.id
        ).send().then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async deleteServiceConfigurationsByBusinessPartnerId(businessPartnerId) {
        return await this._request.delete(
            this._serviceUrl
            + this._configuration + '/'
            + businessPartnerId
        ).send().then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async deleteServiceConfigurationsByServiceProfileId(businessPartnerId, serviceProfileId) {
        return await this._request.delete(
            this._serviceUrl
            + this._configuration + '/'
            + businessPartnerId + '/'
            + serviceProfileId
        ).send().then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async getEventActions() {
        return await this._request.get(this._serviceUrl + this._events + '/').then((response) => {
            return response.body;
        }).catch((error) => {
            return error;
        });
    }

    async checkBackendAvailability() {
        return this._request.get(this._serviceUrl + '/health/check');
    }
}