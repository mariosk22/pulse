import { Component, inject } from '@angular/core';
import {Router, RouterLink } from '@angular/router';
import { FormBuilder,ReactiveFormsModule,Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { first } from 'rxjs';
import { email } from '@angular/forms/signals';

@Component({
  selector: 'app-register',
  standalone:true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb =inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    firstName:['',Validators.required],
    lastName:['',Validators.required],
    email:['',[Validators.required,Validators.email]],
    password:['',[Validators.required,Validators.minLength(8)]]
  });
  errorMessage='';

  OnSubmit():void{
    if(this.form.invalid) return;
    const{firstName,lastName,email,password} = this.form.getRawValue();
    const fullName ='${firstName}${lastName}'.trim();

    this.authService.register({email:email!,password:password!,fullName}).subscribe({
      next:()=> this.router.navigateByUrl('/onBoarding'),
      error:(err)=> this.errorMessage = err.error?.message??'Registration failed'

    });
  }

}

