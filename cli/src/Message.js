export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents, timestamp }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.timestamp = timestamp
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      timestamp: this.timestamp
    })
  }

  getCommandPseudo () {
    if (this.command.startsWith('@')) {
      return this.command.substring(0, 1)
    } else {
      return this.command
    }
  }

  toString () {
    switch (this.getCommandPseudo()) {
      case 'disconnect':
        return (`${this.timestamp}: <${this.username}> has disconnected`)
      case 'connect':
        return `${this.timestamp}: <${this.username}> has connected`
      case 'echo':
        return `${this.timestamp} <${this.username}> (echo): ${this.contents}`
      case 'broadcast':
        return `${this.timestamp} <${this.username}> (broadcast): ${this.contents}`
      case '@':
        return `${this.timestamp} <${this.username}> (whisper): ${this.contents}`
      case 'users':
        return `${this.timestamp}: currently connected users: ${this.contents}`
      case 'usertaken':
        return (`${this.timestamp}: <${this.username}> already exists! Pick something else!`)
      case 'userdoesnotexist':
        return (`${this.timestamp}: <${this.username}> Does not exist! Enter 'users' for a list of current users`)
      default:
        return `Something went wrong`
    }
  }
}
