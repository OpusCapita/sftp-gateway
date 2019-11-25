import React from "react";
import {Components} from '@opuscapita/service-base-ui';
import {Button} from '../component';

class ConfigDataGrid extends Components.ContextComponent {
    state = {};
    columns = [];

    get isDifferent() {
        if (this.state.tmpRows.length !== this.state.rows.length) {
            return true;
        }
        let result = true;

        this.state.rows.forEach((e1, i) => this.state.tmpRows.forEach(e2 => {
                result = e1 !== e2;
            })
        );
        return result;
    };

    constructor(props, context) {
        super();
        this.columns = props.columns
            .filter(column => column.visible !== false);
        this.state = {
            ...props,
            tmpRows: JSON.parse(JSON.stringify(props.rows)),
            edited: false
        };
    };

    EmptyRowsView = () => {
        const message = "No data to show";
        return (
            <div style={{textAlign: "center", backgroundColor: "#cccccc", padding: "100px"}}>
                <img className="img-rounded"
                     alt="OCLogo"
                     src="data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCIgd2lkdGg9IjU5NS4zIiBoZWlnaHQ9IjIwOS44IiB2aWV3Qm94PSIwIDAgNTk1LjMgMjA5LjgiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDU5NS4zIDIwOS44OyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+ICA8ZGVmcz4gICAgPHN0eWxlPi5zdDB7ZmlsbDojRUM2NjA4O30gICAgPC9zdHlsZT4gIDwvZGVmcz48Zz48cGF0aCBjbGFzcz0ic3QwIiBkPSJNOTUuNSw3Mi41YzExLjEsMCwxMy43LDIuNSw5LjgsMjIuOGMtMy45LDIwLjQtNy4zLDIyLjktMTguNSwyMi45Yy0xMS40LDAtMTMuOC0yLjUtOS45LTIyLjlDODAuOCw3NSw4NC4xLDcyLjUsOTUuNSw3Mi41IE04NSwxMjcuOWMyMy43LDAsMjkuMy0xMC4xLDMzLjUtMzIuN2M0LjItMjIuNCwyLjQtMzIuMy0yMS4zLTMyLjNjLTIzLjcsMC0yOS4zLDkuOS0zMy42LDMyLjNDNTkuNSwxMTcuOCw2MS40LDEyNy45LDg1LDEyNy45Ii8+PHBhdGggY2xhc3M9InN0MCIgZD0iTTIwOSwxMDkuOGMtMS40LDcuMi0yLjksOC4yLTEwLjUsOC4yYy03LjcsMC05LjEtMS03LjctOC4ybDUuMi0yNy42YzAuMS0wLjYtMC40LTEuMi0xLjQtMS4yaC05LjVjLTEsMC0xLjcsMC41LTEuOCwxLjJsLTUuNCwyOC42Yy0xLjksMTAuMiwwLjMsMTYuMywxOSwxNi4zYzE4LjYsMCwyMy02LjEsMjQuOS0xNi4zbDUuNC0yOC42YzAuMS0wLjYtMC4yLTEuMi0xLjQtMS4ySDIxNmMtMS4zLDAtMS43LDAuNS0xLjgsMS4yTDIwOSwxMDkuOHoiLz48cGF0aCBjbGFzcz0ic3QwIiBkPSJNMjU1LDg5YzYuMywwLDguOCwwLjcsMTAuOCwwLjdjMS40LDAsMi0wLjUsMi4xLTEuNmwwLjgtNC4xYzAuNS0yLjctMS4xLTMuNi0xMy43LTMuNmMtMTQuMywwLTIwLDIuOC0yMS44LDEyLjVjLTEuNiw4LjUsMy4xLDEwLjQsOC40LDEyLjZsNS45LDIuNGM0LjQsMS44LDUuOSwyLjMsNS4zLDUuNWMtMC43LDMuNS0yLjcsNC43LTkuOCw0LjdjLTcuMywwLTExLjEtMS0xMi42LTFjLTAuOCwwLTEuNSwwLjQtMS42LDEuMWwtMC45LDQuOWMtMC40LDEuOS0wLjIsMi41LDEuMiwzYzEuOCwwLjcsNi45LDEuMSwxMi44LDEuMWMxNiwwLDIxLjktNC45LDIzLjctMTQuNGMxLjQtNy43LTEuNi05LjYtNi44LTExLjhsLTYuNS0yLjZjLTUuMS0yLjEtNy4xLTIuNS02LjUtNS40QzI0Ni40LDg5LjcsMjQ4LjUsODksMjU1LDg5Ii8+PHBhdGggY2xhc3M9InN0MCIgZD0iTTE1Mi4xLDgwLjZjLTEwLjIsMC0xNS41LDEuOC0xOS4yLDQuN2MtMy43LDIuOS01LjUsNy44LTcuNCwxNy42bC04LjEsNDIuOGMtMC4xLDAuNiwwLjIsMS4yLDEuNCwxLjJoOS41YzEuMywwLDEuNy0wLjUsMS44LTEuMmwzLjYtMTkuMmMwLjEtMC40LDAuMy0wLjUsMC44LTAuNWMxLjQsMCwyLjksMS4zLDEwLjcsMS4zYzEzLjEsMCwyMi01LjQsMjUuNS0yMy44QzE3NCw4Ni4yLDE3MSw4MC42LDE1Mi4xLDgwLjYgTTE1Ny41LDEwMy41Yy0yLjIsMTEuNC01LjMsMTQuNi0xMi4zLDE0LjZjLTMuNSwwLTYuNC0wLjQtNy42LTEuNmMtMS4zLTEuNC0xLjEtNC42LDAuMy0xMS45YzEuMi02LjMsMi4yLTEwLjksNC4xLTEyLjVjMS44LTEuNiw0LjctMi4yLDguNS0yLjJDMTU3LjUsODkuOCwxNTkuNiw5Mi4xLDE1Ny41LDEwMy41Ii8+PHBhdGggY2xhc3M9InN0MCIgZD0iTTMwMi42LDExOC43Yy0xMi45LDAtMTYuOS0zLTEzLjEtMjMuNGMzLjgtMjAuMyw5LjMtMjMuMSwyMi4yLTIzLjFjNC4zLDAsMTIuNywwLjksMTIuNywwLjljMC44LDAsMS41LTAuNSwxLjYtMWwxLjEtNmMwLjItMS4xLTAuMi0xLjgtMS42LTIuMWMtMi40LTAuNS02LjQtMS0xMy40LTFjLTI1LjMsMC0zMS42LDkuMS0zNiwzMi4zYy00LjIsMjIuNS0xLjUsMzIuNywyMy44LDMyLjdjNy41LDAsMTEuMy0wLjQsMTMuNy0xLjFjMS44LTAuNSwyLjQtMS41LDIuNy0zbDAuOS00LjljMC4xLTAuNi0wLjQtMS4xLTEuMi0xLjFDMzEzLjksMTE3LjgsMzEwLjUsMTE4LjcsMzAyLjYsMTE4LjciLz48cGF0aCBjbGFzcz0ic3QwIiBkPSJNMzUzLjcsMTA1LjhjMC43LDAsMS4xLDAuMywxLDAuOWwtMC43LDMuNWMtMSw1LjUtNS4zLDguOS0xMy4zLDguOWMtMy42LDAtNS41LTEuMi00LjQtNi43YzEuMi02LjEsMy4zLTYuNiwxMC40LTYuNkgzNTMuN3ogTTM0Ni43LDg5LjFjOC4zLDAsMTEsMS40LDkuOCw3LjljLTAuMiwwLjgtMC42LDEuMi0xLjgsMS4yaC05LjNjLTE1LjMsMC0xOS42LDQuNC0yMS40LDE0LjFjLTIsMTAuNCwwLjcsMTQuOSwxNC41LDE0LjljNC41LDAsMTAuNC0wLjUsMTUuMi00LjdjMC4yLTAuMiwwLjQtMC4zLDAuNS0wLjNjMC4xLDAsMC4xLDAuMSwwLjMsMC4zbDEuMSwyYzEsMS44LDEuNCwyLjIsMy41LDIuMmgyLjljMSwwLDEuNy0wLjUsMS44LTEuMmw0LjctMjQuOGMyLjgtMTQuNiwwLjMtMjAuMi0xOS4yLTIwLjJjLTcuOSwwLTEyLjMsMC45LTE0LjMsMS42Yy0yLjIsMC43LTIuNywxLjYtMywzLjFsLTAuNywzLjZjLTAuMywxLjUsMCwxLjgsMS4yLDEuOEMzMzQuMiw5MC44LDMzOS44LDg5LjEsMzQ2LjcsODkuMSIvPjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik00MzkuMiw3NC4xaDkuNWMxLDAsMS43LTAuNSwxLjgtMS4ybDEuNy04LjljMC4xLTAuNi0wLjQtMS4yLTEuNC0xLjJoLTkuNWMtMSwwLTEuNywwLjUtMS44LDEuMmwtMS43LDguOUM0MzcuNyw3My41LDQzOC4yLDc0LjEsNDM5LjIsNzQuMSBNNDI5LjMsMTI2LjdoOS41YzEsMCwxLjctMC41LDEuOC0xLjJsOC4yLTQzLjJjMC4xLTAuNi0wLjQtMS4yLTEuNC0xLjJoLTkuNWMtMSwwLTEuNywwLjUtMS44LDEuMmwtOC4yLDQzLjJDNDI3LjgsMTI2LjIsNDI4LjMsMTI2LjcsNDI5LjMsMTI2LjciLz48cGF0aCBjbGFzcz0ic3QwIiBkPSJNNTE3LjgsMTA1LjhjMC43LDAsMS4xLDAuMywxLDAuOWwtMC43LDMuNWMtMSw1LjUtNS4zLDguOS0xMy4zLDguOWMtMy42LDAtNS41LTEuMi00LjQtNi43YzEuMi02LjEsMy4zLTYuNiwxMC40LTYuNkg1MTcuOHogTTUxMC43LDg5LjFjOC4zLDAsMTEsMS40LDkuOCw3LjljLTAuMiwwLjgtMC42LDEuMi0xLjgsMS4yaC05LjNjLTE1LjMsMC0xOS42LDQuNC0yMS41LDE0LjFjLTIsMTAuNCwwLjcsMTQuOSwxNC41LDE0LjljNC41LDAsMTAuNC0wLjUsMTUuMi00LjdjMC4xLTAuMiwwLjQtMC4zLDAuNS0wLjNzMC4xLDAuMSwwLjMsMC4zbDEuMSwyYzEsMS44LDEuNCwyLjIsMy41LDIuMmgyLjljMSwwLDEuNy0wLjUsMS45LTEuMmw0LjctMjQuOGMyLjgtMTQuNiwwLjMtMjAuMi0xOS4yLTIwLjJjLTcuOSwwLTEyLjMsMC45LTE0LjMsMS42Yy0yLjIsMC43LTIuNywxLjYtMywzLjFsLTAuNywzLjZjLTAuMywxLjUsMCwxLjgsMS4yLDEuOEM0OTguMiw5MC44LDUwMy45LDg5LjEsNTEwLjcsODkuMSIvPjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik00MDQuMiw4MC42Yy0xMC4yLDAtMTUuNSwxLjgtMTkuMiw0LjdjLTMuNywyLjktNS41LDcuOC03LjQsMTcuNmwtOC4xLDQyLjhjLTAuMSwwLjYsMC4yLDEuMiwxLjQsMS4yaDkuNWMxLjMsMCwxLjctMC41LDEuOC0xLjJsMy42LTE5LjJjMC4xLTAuNCwwLjMtMC41LDAuOC0wLjVjMS40LDAsMi45LDEuMywxMC43LDEuM2MxMy4xLDAsMjItNS40LDI1LjUtMjMuOEM0MjYuMSw4Ni4yLDQyMy4xLDgwLjYsNDA0LjIsODAuNiBNNDA5LjYsMTAzLjVjLTIuMiwxMS40LTUuMywxNC42LTEyLjMsMTQuNmMtMy41LDAtNi40LTAuNC03LjYtMS42Yy0xLjMtMS40LTEuMS00LjYsMC4zLTExLjljMS4yLTYuMywyLjItMTAuOSw0LjEtMTIuNWMxLjgtMS42LDQuNy0yLjIsOC41LTIuMkM0MDkuNiw4OS44LDQxMS43LDkyLjEsNDA5LjYsMTAzLjUiLz48cGF0aCBjbGFzcz0ic3QwIiBkPSJNNDcyLjgsMTE4LjFjLTYuMSwwLTctMi4xLTYuNC01LjNsNC0yMS4zYzAuMS0wLjUsMC42LTAuOCwxLjMtMC44SDQ4NGMwLjcsMCwxLjItMC4zLDEuMy0wLjhsMS40LTcuMmMwLjEtMC41LTAuMy0wLjgtMS0wLjhoLTExLjZjLTEuNSwwLTIuMS0wLjQtMi0xLjVsMC4zLTcuMWMwLTEuNC0wLjQtMS45LTIuMi0xLjloLTcuMWMtMSwwLTEuNywwLjUtMS44LDEuMmwtNy44LDQxLjNjLTEuNyw4LjksMC45LDEzLjUsMTUuOCwxMy41YzkuNiwwLDEwLjktMS40LDExLjMtMy4xbDEtNS4zYzAuMi0wLjgtMC41LTEuMy0xLjMtMS4zQzQ3OC43LDExNy42LDQ3Ni4zLDExOC4xLDQ3Mi44LDExOC4xIi8+PHBhdGggY2xhc3M9InN0MCIgZD0iTTUxNy44LDEwNS44YzAuNywwLDEuMSwwLjMsMSwwLjlsLTAuNywzLjVjLTEsNS41LTUuMyw4LjktMTMuMyw4LjljLTMuNiwwLTUuNS0xLjItNC40LTYuN2MxLjItNi4xLDMuMy02LjYsMTAuNC02LjZINTE3Ljh6IE01MTAuNyw4OS4xYzguMywwLDExLDEuNCw5LjgsNy45Yy0wLjIsMC44LTAuNiwxLjItMS44LDEuMmgtOS4zYy0xNS4zLDAtMTkuNiw0LjQtMjEuNSwxNC4xYy0yLDEwLjQsMC43LDE0LjksMTQuNSwxNC45YzQuNSwwLDEwLjQtMC41LDE1LjItNC43YzAuMS0wLjIsMC40LTAuMywwLjUtMC4zczAuMSwwLjEsMC4zLDAuM2wxLjEsMmMxLDEuOCwxLjQsMi4yLDMuNSwyLjJoMi45YzEsMCwxLjctMC41LDEuOS0xLjJsNC43LTI0LjhjMi44LTE0LjYsMC4zLTIwLjItMTkuMi0yMC4yYy03LjksMC0xMi4zLDAuOS0xNC4zLDEuNmMtMi4yLDAuNy0yLjcsMS42LTMsMy4xbC0wLjcsMy42Yy0wLjMsMS41LDAsMS44LDEuMiwxLjhDNDk4LjIsOTAuOCw1MDMuOSw4OS4xLDUxMC43LDg5LjEiLz48L2c+PC9zdmc+"
                />
                <h3>{message}</h3>
            </div>
        );
    };


    save = () => {
        let _error = [];
        this.state.rows.forEach((row) => {
            if (row.name === "" || row.path === "") {
                _error.push(row);
                row.error = true;
            }
        });
        const _toSave = this.state.rows.filter(row => !row.deleted);
        const _toDelete = this.state.rows.filter(row => row.deleted === true && row.id !== null);

        if (_error.length > 0) {
            // OCAlert.alertError('Something went wrong');
            this.setState({
                rows: this.state.rows,
                edited: true
            });
        } else {
            if (_toSave.length > 0) {
                this.state.onSave(_toSave);
            }
            if (_toDelete.length > 0) {
                this.state.onDelete(_toDelete);
            }
            this.setState({
                rows: Array.from(_toSave),
                tmpRows: Array.from(_toSave),
                edited: false
            });
        }
    };

    /**
     * Context Menu Actions
     */
    deleteRow = (row) => {
        row.deleted = true;
        this.setState({
            edited: this.isDifferent
        });
    };

    onItemClick = (key, row) => {
        console.log(key, row);
        if (key === 'edit') {
            this.state.onEdit(row);
        } else if (key === 'delete') {
            this.deleteRow(row);
        }
    }

    editRow = (item) => {
        this.state.onEdit(item);
        this.setState({
            edited: this.isDifferent
        });
    };

    insertRow = () => {
        this.state.onAddRow();
        this.setState({
            edited: true
        });
    };

    reloadData = () => {
        this.state.onReload();
        this.setState({
            edited: this.isDifferent
        });
    };

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps !== this.props) {
            const _rows = Array.from(this.props.rows);
            this.setState({
                tmpRows: _rows,
                rows: this.props.rows,
                edited: true
            });
        }
        if (prevState.rows !== this.state.rows) {
            let idx = 1;
            this.state.rows.forEach(row => row.position = idx++);
        }
    }

    render() {
        const _rows = (this.state.rows !== null && this.state.rows.length > 0 ? this.state.rows.filter(row => !row.deleted) : []);

        return (
            <div>
                <div className="btn-toolbar" role="toolbar">
                    <div className="btn-group"
                         role="group"
                         style={{display: 'inline'}}>
                        <Button type="button"
                                className="btn btn-primary"
                                onClick={() => this.insertRow()}
                                label="Add"/>
                        <Button type="button"
                                className="btn btn-default"
                                onClick={() => this.reloadData()}
                                label="Reload"/>
                    </div>
                    <div className="btn-group"
                         role="group"
                         style={{display: 'inline', float: 'right'}}>
                        <Button
                            type="submit"
                            className="btn btn-primary"
                            onClick={() => this.save()}
                            label="Save"
                        />
                    </div>
                </div>
                <Components.ListTable
                    columns={this.columns}
                    items={_rows}
                    onButtonClick={this.onItemClick}
                    itemButtons={[
                        {
                            key: 'edit',
                            label: 'Edit'
                        },
                        {
                            key: 'delete',
                            label: 'Delete'
                        }
                    ]}
                />
            </div>
        );
    }
}

export default ConfigDataGrid;