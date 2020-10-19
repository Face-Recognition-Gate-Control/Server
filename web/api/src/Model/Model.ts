import { Client } from 'pg'

/**
 * A Model represents a database handler/querier.
 */
export class Model {
    private _table: string

    private _database: Client

    constructor(table: string, database: Client) {
        this._table = table
        this._database = database
    }

    get table() {
        return this._table
    }

    public get database(): Client {
        return this._database
    }
}
