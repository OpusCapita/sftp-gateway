import React from "react";
import {Components} from "@opuscapita/service-base-ui";
import ReactDataGrid from 'react-data-grid';
import {Menu} from "react-data-grid-addons";
import {Icon} from '@opuscapita/react-icons';
import {requireAll} from "../helper/helper.service";
import {OCAlert} from '@opuscapita/react-alerts';
import {Button} from '../component';

const {ContextMenu, MenuItem, ContextMenuTrigger} = Menu;


export default class ConfigDataGrid extends Components.ContextComponent {
    state = {};
    columns = [];

    defaultProperties = {
        resizable: true,
        filterable: true
    };

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

    icons = requireAll(require.context('!!raw-loader!@opuscapita/svg-icons/lib', true, /.*\.svg$/));

    constructor(props, context) {
        super(props);
        this.columns = props.columns
            .filter(column => column.visible !== false)
            .map(c => ({...c, ...this.defaultProperties}));
        this.state = {
            ...props,
            tmpRows: JSON.parse(JSON.stringify(props.rows)),
            edited: false
        };
    };

    getIcon(name) {
        const ic = this.icons.filter(icon => icon.name === name)[0].svg;
        return ic.default;
    };

    RowRenderer = ({renderBaseRow, ...props}) => {
        let color = props.idx % 2 ? "#EC6608" : "#006070";
        return <div style={{backgroundColor: color}}>{renderBaseRow(props)}</div>;
    };

    EmptyRowsView = () => {
        const message = "No data to show";
        return (
            <div style={{textAlign: "center", backgroundColor: "#EC6608", padding: "100px"}}>
                <Icon type="logo" name="OCLong"/>
                <h3>{message}</h3>
            </div>
        );
    };

    ContextMenu = ({idx, id, rowIdx, onRowDelete, onRowInsert}) => {
        return (<ContextMenu id={id}>
            <MenuItem data={{rowIdx, idx}} onClick={onRowDelete}>
                Delete Row
            </MenuItem>
            <MenuItem data={{rowIdx, idx}} onClick={onRowInsert}>
                Insert
            </MenuItem>
        </ContextMenu>);
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
            OCAlert.alertError('Something went wrong');
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
            OCAlert.alertSuccess('The operation was success!');
        }
    };

    /**
     * Context Menu Actions
     */
    deleteRow = (rowIdx) => {
        let nextRows = Array.from(this.state.rows);
        nextRows.filter(row => !row.deleted)[rowIdx].deleted = true;
        return nextRows;
    };

    editRow = (rowIdx) => {
        this.state.onEdit(this.state.rows.filter(row => !row.deleted)[rowIdx]);
        this.setState({
            edited: this.isDifferent
        });
        console.log(this.state);
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
        OCAlert.alertSuccess('The operation was success!');
    };

    sortRows = (initialRows, sortColumn, sortDirection) => {
        let _sortedRows = Array.from(initialRows);
        const comparer = (a, b) => {
            if (sortDirection === "ASC") {
                return a[sortColumn] > b[sortColumn] ? 1 : -1;
            } else if (sortDirection === "DESC") {
                return a[sortColumn] < b[sortColumn] ? 1 : -1;
            }
        };
        return sortDirection === "NONE" ? initialRows : [..._sortedRows].sort(comparer);
    };

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps !== this.props) {
            console.log(this.state);
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
                            // disabled={!this.state.edited}
                            onClick={() => this.save()}
                            label="Save"
                        />
                        <Button
                            type="button"
                            className="btn btn-default"
                            // disabled={!this.state.edited}
                            onClick={() => this.setState({
                                rows: JSON.parse(JSON.stringify(this.state.tmpRows)),
                                edited: false
                            })}
                            label="Cancel"
                        />
                    </div>
                </div>
                <ReactDataGrid
                    columns={this.columns}
                    rowGetter={idx => this.state.rows.filter(row => !row.deleted)[idx]}
                    rowsCount={this.state.rows.filter(row => !row.deleted).length}
                    rowRenderer={this.RowRenderer}
                    onRowDoubleClick={(idx) => this.editRow(idx)}
                    enableRowSelect={false}
                    enableCellSelect={false}
                    enableCellAutoFocus={false}
                    onGridSort={(sortColumn, sortDirection) => {
                        const _rows = this.sortRows(this.state.rows, sortColumn, sortDirection);
                        this.setState({rows: _rows});
                    }}
                    contextMenu={
                        <this.ContextMenu
                            onRowDelete={(e, {rowIdx}) => this.setState({
                                rows: this.deleteRow(rowIdx),
                                edited: true
                            })}
                            onRowInsert={() => this.insertRow()}
                        />
                    }
                    emptyRowsView={this.EmptyRowsView}
                    RowsContainer={ContextMenuTrigger}
                />
            </div>
        );
    }
}
