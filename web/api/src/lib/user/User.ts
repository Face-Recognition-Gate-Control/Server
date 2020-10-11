type UserType = {
    id: number
    firstname: string
    lastname: string
    email: string
    telephone: number
    password: string
    created: number
}

export class User {
    constructor({ id, firstname, lastname, email, telephone, password, created }: UserType) {}
}
