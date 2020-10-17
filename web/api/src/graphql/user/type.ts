import { GraphQLInt, GraphQLObjectType, GraphQLString } from 'graphql'
import { UserEnterEvents, UserRoles } from './query'

/**
 * Describes the user type for GraphQL.
 * This is required for GraphQL to understand how a user "looks like" and what
 * types each field has.
 */
let UserType = new GraphQLObjectType({
    name: 'UserType',
    fields: () => ({
        id: { type: GraphQLInt },
        firstname: { type: GraphQLString },
        lastname: { type: GraphQLString },
        email: { type: GraphQLString },
        telephone: { type: GraphQLInt },
        password: { type: GraphQLString },
        created: { type: GraphQLString },
        roles: UserRoles,
        enterEvents: UserEnterEvents,
    }),
    description: 'A registered user',
})

/**
 * Describes a user entered event
 * The event will occur when a user enters a gate/station
 */
let UserEnterEvent = new GraphQLObjectType({
    name: 'UserEnterEvent',
    fields: () => ({
        station_id: { type: GraphQLString },
        enter_time: { type: GraphQLString },
    }),
    description: 'An event for when a user entered a gate/station',
})

export { UserType, UserEnterEvent }
