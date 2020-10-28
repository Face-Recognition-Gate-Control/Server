import { User } from '@/lib/user/User'

export class Authorizer {
    private _user: User | undefined

    public set user(user: User | undefined) {
        if (!this._user) {
            this._user = user
        }
    }

    public hasRole(roles: Array<string>) {
        if (this._user) {
            return roles.find((role) => this._user!.roles?.find((userRole) => userRole === role))
        }
    }

    public isOwner(id: string | number) {
        if (!this._user || !this._user.id) return false
        return id === this._user.id
    }

    public isAuthorized() {
        console.log(this._user)

        return this._user
    }
}
