function addToCart(pid,pname,pprice){
    cart=localStorage.getItem("cart");
    if(cart==null){
         products=[];
        product={id:pid,name:pname,price:pprice,count:1};
        products.push(product);
        localStorage.setItem("cart",JSON.stringify(products));
        swal.fire({
            toast:"true",
            background:"#ffe996",
            html:"<h6 class='text-dark text-small px-1'>First Product Added To Cart</h6>",
            position:"bottom",
            
            showConfirmButton:false,
            timer:"2000",
            timerProgressBar:true,
        })
    }else{
         pcart=JSON.parse(cart);
         oldcart=pcart.find((item)=> item.id==pid);
        if(oldcart){
            
            pcart.map((item)=>{ 
                    
              if(item.id == oldcart.id){
                         item.count++;
               }
            })
               
             localStorage.setItem("cart",JSON.stringify(pcart));
            
              swal.fire({
            toast:"true",
            background:"#ffe996",
            html:"<h6 class='text-dark text-small px-1'>Book quentity incresed...</h6>",
            position:"bottom",    
            showConfirmButton:false,
            timer:"2000",
            timerProgressBar:true,
        })
         
        }else{
           p={id:pid,name:pname,price:pprice,count:1};
           pcart.push(p);
           localStorage.setItem("cart",JSON.stringify(pcart));
          
           swal.fire({
            toast:"true",
            background:"#ffe996",
            html:"<h6 class='text-dark text-small px-1'>New book add to cart..</h6>",
            position:"bottom",
            
            showConfirmButton:false,
            timer:"2000",
            timerProgressBar:true,
        })
        }
   }
   updateCart();
}

function updateCart(){
   
   cart=JSON.parse(localStorage.getItem("cart"));
   if(cart==null || cart.length==0 ){
       
       
       $(".cart-num").html("( 0 )");
       $(".cart-body").html("Your cart is empty...");
      // $(".data").html("Select the Disire product for buy ....");
       $(".checkout-btn").addClass("disabled");
      // $(".check-btn2").addClass("d-none");
   }else{
       
       $(".cart-num").html(`(  ${cart.length}   )`);
       $(".checkout-btn").removeClass("disabled");
      // $(".check-btn2").removeClass("d-none");
       
       
       table=`
       
       <table class="table table-hover">
           <tr class="text-style">
             
               <th>Name</th>
               <th>Price</th>
               <th>Quaantity</th>
               <th>Total</th>
               <th>Action</th>
              
           </tr>
       
       `;
       
       
      var totalPrice=0;
       cart.map((item)=>{



           table+=`
               <tr>
                   
                   <td><small>${item.name}</small></td>
                   <td><small>${item.price}</small></td>
                   <td><small>${item.count}</small></td>
                   <td><small>${item.price*item.count}</small></td>
                  
                   <td>
                       <button class="btn btn-danger btn-sm" onclick=" removeBook(${item.id})">Remove</button>
                   </td>
                  
                   
               </tr>
           
           `;
           totalPrice+=item.price * item.count;
           
       })
              
       table=table+`  
              <tr>
                   <td colspan='5' class='text-right'>Total Price : ${totalPrice} </td>
               </tr>
               </table>
      
       
       `;
       $(".cart-body").html(table);
       
   }
}
function removeBook(pid){
    
   cart=JSON.parse(localStorage.getItem("cart"));
   updatecart=cart.filter((item)=> item.id!=pid);
   n=`${updatecart.length}`;
   localStorage.setItem("cart",JSON.stringify(updatecart));
   if(n==0){
   localStorage.removeItem("cart");
   }
   updateCart();
    swal.fire({
            toast:"true",
            background:"#fecc0f",
            html:"<h6 class='text-dark text-small px-1'>Book remove from cart!! </h6>",
            position:"bottom",
            
            showConfirmButton:false,
            timer:"1000",
            timerProgressBar:true,
        })
}
$(document).ready(function(){
    updateCart();
})


