import { Component, OnInit } from '@angular/core';
import { MqttService } from 'ngx-mqtt';

@Component({
  selector: 'app-publish',
  templateUrl: './publish.component.html',
  styleUrls: ['./publish.component.css'],
})
export class PublishComponent implements OnInit {
  topic: string;
  constructor(private readonly mqttService: MqttService) {}

  ngOnInit(): void {}

  publishMessage(message: string): void {
    if (!this.topic) {
      return;
    }
    this.mqttService.publish(this.topic, message);
  }
}
