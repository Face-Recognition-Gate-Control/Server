import Logger from 'bunyan'
import bunyan from 'bunyan'

const loggerName = 'STDLOGGER'
/**
 * Instantiate a new bunyan logger
 */
const BunyanLogger = bunyan.createLogger({
    name: loggerName,
    stream: process.stdout,
})

class ConsoleLogger {
    display(any?: any, message?: any) {
        console.log(message)

        console.log('====================================================\n')
        if (message) {
            console.log(message)
            console.log('____________________________________________________')
        }
        if (any) console.log(any)
        console.log('====================================================\n')
    }

    public info(any?: any, message?: any) {
        this.display(any, message)
    }

    public warn(any?: any, message?: any) {
        this.display(any, message)
    }

    public error(any?: any, message?: any) {
        console.log(message)

        this.display(any, message)
    }

    public fatal(any?: any, message?: any) {
        this.display(any, message)
    }
}
const Logger2 = new ConsoleLogger()
export default Logger2
