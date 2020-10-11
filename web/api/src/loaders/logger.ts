import bunyan from 'bunyan'

const loggerName = 'STDLOGGER'
/**
 * Instantiate a new bunyan logger
 */
const Logger = bunyan.createLogger({
    name: loggerName,
    stream: process.stdout,
})
export default Logger
