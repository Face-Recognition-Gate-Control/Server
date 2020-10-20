import { Roles } from '@/lib/auth/roles'
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
        id: {
            type: GraphQLString,
        },
        firstname: {
            type: GraphQLString,
            resolve: (root, args, ctx) => {
                if (
                    ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator, Roles.User]) ||
                    ctx.authorizer.isOwner(root.id)
                ) {
                    return root.firstname
                }
            },
        },
        lastname: {
            type: GraphQLString,
            resolve: (root, args, ctx) => {
                if (
                    ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator, Roles.User]) ||
                    ctx.authorizer.isOwner(root.id)
                ) {
                    return root.lastname
                }
            },
        },
        email: {
            type: GraphQLString,
            resolve: (root, args, ctx) => {
                console.log('HEI')
                console.dir(ctx.authorizer)

                if (
                    ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator]) ||
                    ctx.authorizer.isOwner(root.id)
                ) {
                    return root.email
                }
            },
        },
        telephone: {
            type: GraphQLInt,
            resolve: (root, args, ctx) => {
                if (
                    ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator]) ||
                    ctx.authorizer.isOwner(root.id)
                ) {
                    return root.telephone
                }
            },
        },
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
