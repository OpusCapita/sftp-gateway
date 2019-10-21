import React from "react";
import {Components} from "@opuscapita/service-base-ui";
import {Button} from '@opuscapita/react-buttons';
import ReactDataGrid from 'react-data-grid';
import {Menu, ToolsPanel} from "react-data-grid-addons";
import {Icon} from '@opuscapita/react-icons';
import {requireAll} from "../helper/helper.service";

const {ContextMenu, MenuItem, SubMenu, ContextMenuTrigger} = Menu;
const Toolbar = ToolsPanel.AdvancedToolbar;


export class ConfigDataGrid extends Components.ContextComponent {
    state = {};
    columns = [];

    defaultProperties = {
        resizable: true,
        filterable: true
    };

    icons = requireAll(require.context('!!raw-loader!@opuscapita/svg-icons/lib', true, /.*\.svg$/));

    constructor(props, context) {
        super(props);
        this.columns = props.columns.map(c => ({...c, ...this.defaultProperties}));
        this.state = {
            ...props,
            tmpRows: props.rows,
            edited: false
        };
    };

    getIcon(name) {
        let icon = this.icons.filter(icon => icon.name === name)[0].svg;
        return icon.default;
    };

    EmptyRowsView = () => {
        const message = "No data to show";
        return (
            <div style={{textAlign: "center", backgroundColor: "#ddd", padding: "100px"}}>
                <Icon type="logo" name="OCLong"/>
                <h3>{message}</h3>
            </div>
        );
    };

    ContextMenu = ({idx, id, rowIdx, onRowDelete, onRowInsertAbove, onRowInsertBelow}) => {
        return (<ContextMenu id={id}>
            <MenuItem data={{rowIdx, idx}} onClick={onRowDelete}>
                Delete Row
            </MenuItem>
            <SubMenu title="Insert Row">
                <MenuItem data={{rowIdx, idx}} onClick={onRowInsertAbove}>
                    Above
                </MenuItem>
                <MenuItem data={{rowIdx, idx}} onClick={onRowInsertBelow}>
                    Below
                </MenuItem>
            </SubMenu>
        </ContextMenu>);
    };

    /**
     * Context Menu Actions
     */
    deleteRow = (rowIdx) => {
        let nextRows = this.rows;
        nextRows.splice(rowIdx, 1);
        return nextRows;
    };

    insertRow = (rowIdx) => {
        const newRow = this.createNewRow(rowIdx)
        const nextRows = [...this.state.tmpRows];
        nextRows.splice(rowIdx, 0, newRow);
        return nextRows;
    };

    onGridRowsUpdated = ({fromRow, toRow, updated}) => {
        this.setState(state => {
            const rows = state.slice();
            for (let i = fromRow; i <= toRow; i++) {
                rows[i] = {...rows[i], ...updated};
            }
            return {rows};
        });
    };

    createNewRow(idx) {
        return {
            id: idx,
            businessPartnerId: "",
            serviceProfileId: "",
            name: "",
            description: "",
            path: ""
        }
    }

    sortRows = (initialRows, sortColumn, sortDirection) => rows => {
        const comparer = (a, b) => {
            if (sortDirection === "ASC") {
                return a[sortColumn] > b[sortColumn] ? 1 : -1;
            } else if (sortDirection === "DESC") {
                return a[sortColumn] < b[sortColumn] ? 1 : -1;
            }
        };
        return sortDirection === "NONE" ? initialRows : [...rows].sort(comparer);
    };

    render() {
        return (
            <ReactDataGrid
                columns={this.columns}
                rowGetter={i => this.state.tmpRows[i]}
                rowsCount={this.state.tmpRows.length}
                // minHeight={150}
                onGridRowsUpdated={this.onGridRowsUpdated}
                enableCellSelect={false}
                onGridSort={(sortColumn, sortDirection) =>
                    this.state.tmpRows = this.sortRows(this.state.tmpRows, sortColumn, sortDirection)
                }
                contextMenu={
                    <this.ContextMenu
                        onRowDelete={(e, {rowIdx}) => this.setState({tmpRows: this.deleteRow(rowIdx)})}
                        onRowInsertAbove={(e, {rowIdx}) => this.setState({tmpRows: this.insertRow(rowIdx)})}
                        onRowInsertBelow={(e, {rowIdx}) => this.setState({tmpRows: this.insertRow(rowIdx + 1)})}
                    />
                }
                toolbar={
                    <Toolbar enableFilter={true}>
                        <div className='row'>
                            <div style={{display: 'inline'}} className='m-md-2'>
                                <Button
                                    svg={this.getIcon('add')}
                                    onClick={() => {
                                        this.setState({tmpRows: this.insertRow(this.state.tmpRows.length + 1)})
                                    }}
                                    paper={true}
                                />
                                <Button
                                    svg={this.getIcon('refresh')}
                                    onClick={() => this.setState({tmpRows: this.state.rows})}
                                    paper={true}
                                />
                                <Button
                                    svg={this.getIcon('filter')}
                                    onClick={() => console.log('Filter button click!')}
                                    paper={true}
                                />
                            </div>
                            <div style={{display: 'inline', float: 'right'}} className='m-md-2'>
                                <Button
                                    svg={this.getIcon('save')}
                                    onClick={() => this.setState({rows: this.state.tmpRows})}
                                    paper={true}
                                />
                                <Button
                                    svg={this.getIcon('cancel')}
                                    onClick={() => this.setState({tmpRows: this.state.rows})}
                                    paper={true}
                                />
                            </div>
                        </div>
                    </Toolbar>
                }
                emptyRowsView={this.EmptyRowsView}
                RowsContainer={ContextMenuTrigger}
            />
        );
    }
}
