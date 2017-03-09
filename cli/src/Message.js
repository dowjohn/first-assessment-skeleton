export class Message {
  static fromJSON (buffer) {
    return new Message(JSON.parse(buffer.toString()))
  }

  constructor ({ username, command, contents, timeStamp }) {
    this.username = username
    this.command = command
    this.contents = contents
    this.timeStamp = timeStamp
  }

  toJSON () {
    return JSON.stringify({
      username: this.username,
      command: this.command,
      contents: this.contents,
      timeStamp: this.timeStamp
    })
  }

  getCommandPsudo () {
    if (this.command.startsWith('@')) {
      return this.command.substring(0, 1)
    } else {
      return this.command
    }
  }

  toString () {
    switch (this.getCommandPsudo()) {
      case 'disconnect':
        return (`${this.timeStamp}: <${this.username}> has disconnected`)
      case 'connect':
        return `${this.timeStamp}: <${this.username}> has connected`
      case 'echo':
        return `${this.timeStamp} <${this.username}> (echo): ${this.contents}`
      case 'broadcast':
        return `${this.timeStamp} <${this.username}> (broadcast): ${this.contents}`
      case '@':
        return `${this.timeStamp} <${this.username}> (whisper): ${this.contents}`
      case 'users':
        return `${this.timeStamp}: currently connected users: ${this.contents}`
      default:
        return `Something went wrong`
    }
  }
}
