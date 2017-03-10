import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server
let commando

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username> [host] [port]')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username
    let host = args.host
    let port = args.port
    if (host === undefined || port === undefined) {
      host = 'localhost'
      port = 8080
    }
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
    if (command === 'disconnect') {
      commando = command
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      commando = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      commando = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'users') {
      commando = command
      server.write(new Message({ username, command }).toJSON() + '\n')
    } else if (command.startsWith('@')) {
      commando = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (commando !== undefined) {
      if (commando === 'echo' ||
        commando === 'broadcast' ||
        commando === 'users' ||
        commando.startsWith('@')) {
        contents = command + ' ' + contents
        command = commando
        let messy = new Message({ username, command, contents })
        server.write(messy.toJSON() + '\n')
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
