import { Injectable } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {environment} from "../../../environments/environment";
import {BehaviorSubject} from "rxjs";
import {UserService} from "../../shared/services/user.service";


@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Stomp.Client;
  private socket: WebSocket;
  private isConnected = new BehaviorSubject<boolean>(false);
  private subscriptions: Map<string, Stomp.StompSubscription> = new Map<string, Stomp.StompSubscription>();
  private messages: Map<string, BehaviorSubject<string>> = new Map<string, BehaviorSubject<string>>();

  constructor(private userService: UserService) {}

  createAndConnect(topic?: string): void {
    this.refreshAccessToken(() => {
      this.configureClient(topic);
      this.connect();
    });
  }

  configureClient(topic?: string): void {
    const that = this;
    this.socket = new SockJS(environment.brokerUrl);
    this.stompClient = Stomp.Stomp.over(() => {
      return that.socket
    });
    this.stompClient.configure({connectionTimeout: 10000});
    this.stompClient.reconnectDelay = 1000;

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      that.isConnected.next(true);
      if (topic) {
        that.subscribe(topic);
      }
    };

    this.stompClient.onDisconnect = (frame) => {
      console.log("Disconnected: " + frame);
      that.isConnected.next(false);
      that.subscriptions.forEach((subscription) => subscription.unsubscribe());
      that.subscriptions.clear();
    };

    this.stompClient.onWebSocketError = (error) => {
      console.error('Error with websocket', error);
      that.isConnected.next(false);
    };
  }

  getConnectedStatus(): BehaviorSubject<boolean> {
    return this.isConnected;
  }

  getMessage(topic: string): BehaviorSubject<string> {
    if(!this.messages.get(topic)) {
      this.messages.set(topic, new BehaviorSubject<string>(''));
    }
    return <BehaviorSubject<string>> this.messages.get(topic);
  }

  send(destination: string, message: string): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: destination,
        body: message
      });
    } else {
      console.error('WebSocket not connected.');
    }
  }

  subscribe(topic: string): void{
    if(!this.messages.get(topic)) {
      this.messages.set(topic, new BehaviorSubject<string>(''));
    }
    const subscription = this.stompClient.subscribe(topic, (message) => {
      console.log("Message received: " + message.body);
      const receivedMessage = message.body;
      this.messages.get(topic)!.next(receivedMessage);
    });
    this.subscriptions.set(topic, subscription);
  }

  unsubscribe(topic: string): void {
    const subscription = this.subscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(topic);
    }
  }

  connect(): void {
    const accessToken = this.userService.getAccessToken();
    if (accessToken) {
      this.stompClient.connectHeaders = {
        Authorization: `Bearer ${accessToken}`
      };
    }
    this.stompClient.activate();
  }

  disconnect(): void {
    this.stompClient.deactivate();
  }

  private refreshAccessToken(resolve: () => void): void {
    const at = this.userService.getAccessToken();
    const isAccessTokenValid = at && this.userService.isTokenValid(at);
    if (this.userService.isLoggedIn && !isAccessTokenValid) {
      this.userService.refreshTokens()
        .subscribe({
          next: () => {
            console.log("Access token refreshed")
            resolve();
          },
          error: err => {
            console.log(err);
            this.userService.logout();
          }
        });
    } else {
      resolve();
    }
  }

}
