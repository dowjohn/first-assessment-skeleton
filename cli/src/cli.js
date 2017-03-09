import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server

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
      const mess = Message.fromJSON(buffer)
      switch (mess.getCommandPsudo()) {
        case 'connect':
          this.log(cli.chalk['yellow'](mess.toString()))
          break
        case 'disconnect':
          this.log(cli.chalk['red'](mess.toString()))
          break
        case 'echo':
          this.log(cli.chalk['red'](mess.toString()))
          break
        case 'broadcast':
          this.log(cli.chalk['green'](mess.toString()))
          break
        case '@':
          this.log(cli.chalk['yellow'](mess.toString()))
          break
        case 'users':
          this.log(cli.chalk['red'](mess.toString()))
          break
        default:
          this.log('something went wrong!')
          break
      }
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    const [ command, ...rest ] = words(input, /[^, ]+/g)
    const contents = rest.join(' ')
    // addressee can be either 'all' or a specific user
    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'users') {
      server.write(new Message({ username, command }).toJSON() + '\n')
    } else if (command.startsWith('@')) {
      console.log(command)
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else {
      this.log(`Command <${command}> was not recognized`)
    }
    callback()
  })
