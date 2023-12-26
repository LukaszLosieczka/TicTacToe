import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../shared/services/user.service";
import {first} from "rxjs";
import {ConfirmationToken} from "../../model/ConfirmationToken";

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrl: './confirm.component.css'
})
export class ConfirmComponent implements OnInit{

  form: FormGroup;
  loading = false;
  submitted = false;
  isError = false;
  errorMessage = "";
  confirmed = false;

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.form = new FormGroup({
      username: new FormControl("", [Validators.required]),
      code: new FormControl("", [Validators.required])
    });
  }

  get f() { return this.form.controls; }

  onSubmit(): void {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    const token: ConfirmationToken = {login: this.form.get("username")?.value, value: this.form.get("code")?.value};
    this.userService.confirmRegistration(token)
      .pipe(first())
      .subscribe({
        next: () => {
          this.confirmed = true;
        },
        error: error => {
          this.isError = true;
          this.errorMessage = error.error;
          this.loading = false;
        }
      });
  }

}
