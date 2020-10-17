export type UserType = {
    id?: number
    firstname?: string
    lastname?: string
    email?: string
    telephone?: number
    password?: string
    created?: number
    roles?: Array<string>
}

export class User {
    private _id?: number
    private _firstname?: string
    private _lastname?: string
    private _email?: string
    private _telephone?: number
    private _password?: string
    private _created?: number
    private _roles?: Array<string>

    constructor({ id, firstname, lastname, email, telephone, password, created, roles }: UserType) {
        this._id = id
        this._firstname = firstname
        this._lastname = lastname
        this._email = email
        this._telephone = telephone
        this._password = password
        this._created = created
        this._roles = roles
    }

    public get id(): number | undefined {
        return this._id
    }

    public get firstname(): string | undefined {
        return this._firstname
    }

    public get lastname(): string | undefined {
        return this._lastname
    }

    public get email(): string | undefined {
        return this._email
    }

    public get telephone(): number | undefined {
        return this._telephone
    }

    public get created(): number | undefined {
        return this._created
    }

    public get password(): string | undefined {
        return this._password
    }

    public get roles(): Array<string> | undefined {
        return this._roles
    }
}
