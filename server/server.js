const WebSocketServer = require('ws').Server;
const keypress = require('keypress');

const wss = new WebSocketServer({ port: 9001 });

let lat = 53.7282337;
let long = -1.8642777;
let alt = 5;

keypress(process.stdin);

wss.on('connection', ws => {
    console.log('Client connected');
    ws.on('close', () => process.exit(0));
    process.stdin.on('keypress', (ch, key) => {
        console.log(`Got keypress: ${JSON.stringify(key)}`);
        if(key && key.ctrl && key.name === 'c') {
            process.exit(0);
        }
        switch(key.name) {
            case 'up':
                lat += 0.0000100;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'down':
                lat -= 0.0000100;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'left':
                long -= 0.0000100;
                ws.send(`${lat}:${long}:${alt}`);
                break;
            case 'right':
                long += 0.0000100;
                ws.send(`${lat}:${long}:${alt}`);
                break;
        }
    });
});


process.stdin.setRawMode(true);
process.stdin.resume();
