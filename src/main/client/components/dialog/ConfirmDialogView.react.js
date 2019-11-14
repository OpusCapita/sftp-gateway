import React from "react";
import {Components} from "@opuscapita/service-base-ui";
import ConfirmDialog from '@opuscapita/react-confirmation-dialog';

export default class ConfirmDialogView extends Components.Component {

    constructor(props) {
        super(props);
        this.state = {
            showConfirmationDialog: false
        }
    }

    hideConfirmDialog = () => {
        this.setState({
            showConfirmationDialog: false
        });
    }

    showConfirmDialog = () => {
        this.setState({
            showConfirmationDialog: true
        });
    }

    render() {
        return (
            this.state.showConfirmationDialog &&
            <ConfirmDialog
                localizationTexts={{
                    title: 'Confirmation',
                    body: 'Are you sure you want to do this?',
                }}
                cancelCallback={this.hideConfirmDialog}
                confirmCallback={this.confirmAction}
            />
        );
    }
}