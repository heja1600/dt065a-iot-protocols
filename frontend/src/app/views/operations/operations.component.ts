import { Component, OnInit } from '@angular/core';

export enum OperationPage {
  SUBSCRIBE = 0,
  PUBLISH = 1,
}
@Component({
  selector: 'app-operations',
  templateUrl: './operations.component.html',
  styleUrls: ['./operations.component.css'],
})
export class OperationsComponent implements OnInit {
  operationPage: OperationPage;
  constructor() {}

  ngOnInit(): void {
    this.operationPage = OperationPage.SUBSCRIBE;
  }
}
