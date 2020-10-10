import expressLoader from '@/loaders/express'

/**
	Loads all application requirements
*/
export default async ({ express }: { express: Express.Application }) => {
    expressLoader({ server: express })
}
