export interface StationType {
    id?: string
    login_key?: string
    station_name?: string
    last_checking?: number
}

export class Station {
    private _id?: string
    private _login_key?: string
    private _station_name?: string
    private _last_checking?: number

    constructor({ id, login_key, station_name, last_checking }: StationType) {
        this._id = id
        this._login_key = login_key
        this._station_name = station_name
        this._last_checking = last_checking
    }

    public get id(): string | undefined {
        return this._id
    }

    public get login_key(): string | undefined {
        return this._login_key
    }

    public get station_name(): string | undefined {
        return this._station_name
    }

    public get last_checkin(): number | undefined {
        return this._last_checking
    }
}
