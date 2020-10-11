import query from '@/graphql/user/query'
import mutation from '@/graphql/user/mutation'
import { UserModel } from '@/Model/UserModel'
import { UserService } from '@/Service/UserService'

const userService = new UserService(new UserModel())

export default {
    query: query(userService),
    mutation,
}
