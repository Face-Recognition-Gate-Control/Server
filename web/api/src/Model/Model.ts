/**
 * A Model represents a database handler/querier.
 */
export class Model {
    private _table: string

    constructor(table: string) {
        this._table = table
    }

    get table() {
        return this._table
    }
}
