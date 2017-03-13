import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let previousCommand

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username> [host] [port]')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function ({ username: user, host = 'localhost', port = 8080}, callback) {
    username = user
    server = connect({ host: host, port: port }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })

    server.on('data', (buffer) => {
      this.log(Message.fromJSON(buffer).toString())
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    let [ command, ...rest ] = words(input, /[^, ]+/g)
    let contents = rest.join(' ')
    if (command === 'disconnect' || command === 'echo' || command === 'broadcast' || command.startsWith('@') || command === 'users') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (previousCommand !== undefined) {
      if (previousCommand === 'echo' ||
        previousCommand === 'broadcast' ||
        previousCommand === 'users' ||
        previousCommand.startsWith('@')) {
        contents = command + ' ' + contents
        command = previousCommand
        server.write(new Message({ username, command, contents }).toJSON() + '\n')
      } else {
        this.log(
          `<${command}> not recognized.
Available commands include: 'users', 'disconnect', 'echo', 'broadcast', '@username'`)
      }
    } else {
      this.log(
        `<${command}> not recognized.
Available commands include: 'users', 'disconnect', 'echo', 'broadcast', '@username'`)
    } callback()
  })
