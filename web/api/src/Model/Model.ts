import { table } from 'console'

export class Model {
    private _table: string

    constructor(table: string) {
        this._table = table
    }

    get table() {
        return this._table
    }
}
