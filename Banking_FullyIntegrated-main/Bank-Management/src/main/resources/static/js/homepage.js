const menuBtn = document.querySelector(".menu-btn");
	const navigation = document.querySelector(".navigation");

	menuBtn.addEventListener("click", () => {
		menuBtn.classList.toggle("active");
		navigation.classList.toggle("active");
	});

	const btns = document.querySelectorAll(".nav-btn");
	const slides = document.querySelectorAll(".video-slide");

	var sliderNav = function(manual) {
		btns.forEach((btn) => {
			btn.classList.remove("active");
		});

		slides.forEach((slide) => {
			slide.classList.remove("active");
		});

		btns[manual].classList.add("active");
		slides[manual].classList.add("active");
	};

	btns.forEach((btn, i) => {
		btn.addEventListener("click", () => {
			sliderNav(i);
		});
	});
	
	document.querySelectorAll('.tilt-card').forEach(card => {
	  card.addEventListener('mousemove', e => {
	    const rect = card.getBoundingClientRect();
	    const x = e.clientX - rect.left - rect.width / 2;
	    const y = e.clientY - rect.top - rect.height / 2;
	    card.style.transform = `rotateX(${y / -20}deg) rotateY(${x / 20}deg) scale(1.02)`;
	  });
	  card.addEventListener('mouseleave', () => {
	    card.style.transform = 'rotateX(0deg) rotateY(0deg) scale(1)';
	  });
	});

	document.addEventListener("DOMContentLoaded", () => {
	  // Intersection Observer for Service Cards
	  const serviceObserver = new IntersectionObserver((entries, observer) => {
	    entries.forEach(entry => {
	      if (entry.isIntersecting) {
	        entry.target.classList.add("animate");
	        observer.unobserve(entry.target);
	      }
	    });
	  }, { threshold: 0.2 });

	  document.querySelectorAll(".service-card").forEach(card => {
	    serviceObserver.observe(card);
	  });

	  // Intersection Observer for Account Cards
	  const accountObserver = new IntersectionObserver((entries, observer) => {
	    entries.forEach(entry => {
	      if (entry.isIntersecting) {
	        entry.target.classList.add("animate");
	        observer.unobserve(entry.target);
	      }
	    });
	  }, { threshold: 0.2 });

	  document.querySelectorAll(".account-card").forEach(card => {
	    accountObserver.observe(card);
	  });

	  // Swiper init
	  const swiper = new Swiper(".mySwiper", {
	    slidesPerView: 1,
	    spaceBetween: 20,
	    loop: true,
	    grabCursor: true,
	    pagination: {
	      el: ".swiper-pagination",
	      clickable: true
	    },
	    navigation: {
	      nextEl: ".swiper-button-next",
	      prevEl: ".swiper-button-prev"
	    },
	    breakpoints: {
	      640: { slidesPerView: 1.2 },
	      768: { slidesPerView: 2 },
	      1024: { slidesPerView: 3 }
	    }
	  });

	  // Optional Tilt effect
	  document.querySelectorAll('.tilt-card').forEach(card => {
	    card.addEventListener('mousemove', e => {
	      const rect = card.getBoundingClientRect();
	      const x = e.clientX - rect.left - rect.width / 2;
	      const y = e.clientY - rect.top - rect.height / 2;
	      card.style.transform = `rotateX(${y / -20}deg) rotateY(${x / 20}deg) scale(1.02)`;
	    });
	    card.addEventListener('mouseleave', () => {
	      card.style.transform = 'rotateX(0deg) rotateY(0deg) scale(1)';
	    });
	  });
	});
	

	//EMI Calculator
	function calculateEMI() {
	  const loanAmount = parseFloat(document.getElementById("loanAmount").value);
	  const annualInterest = parseFloat(document.getElementById("interestRate").value);
	  const tenureYears = parseInt(document.getElementById("loanTenure").value);

	  if (isNaN(loanAmount) || isNaN(annualInterest) || isNaN(tenureYears)) {
	    document.getElementById("emiResult").innerText = "Please enter valid inputs.";
	    return;
	  }

	  const monthlyInterest = annualInterest / 12 / 100;
	  const totalMonths = tenureYears * 12;

	  const emi = (loanAmount * monthlyInterest * Math.pow(1 + monthlyInterest, totalMonths)) /
	              (Math.pow(1 + monthlyInterest, totalMonths) - 1);

	  document.getElementById("emiResult").innerText = `Monthly EMI: ₹${emi.toFixed(2)}`;
	}
	
	window.addEventListener("load", function () {
	   const container = document.getElementById("testimonial-container");
	   const scrollLeftBtn = document.getElementById("scroll-left");
	   const scrollRightBtn = document.getElementById("scroll-right");

	   scrollLeftBtn.addEventListener("click", () => {
	     container.scrollBy({ left: -320, behavior: "smooth" });
	   });

	   scrollRightBtn.addEventListener("click", () => {
	     container.scrollBy({ left: 320, behavior: "smooth" });
	   });

	   // Animate cards after load
	   const cards = document.querySelectorAll(".testimonial-card");
	   cards.forEach((card, index) => {
	     setTimeout(() => {
	       card.style.opacity = "1";
	     }, 200 * index);
	   });
	 });
	 
	 document.querySelectorAll(".faq-question").forEach((button) => {
	   button.addEventListener("click", () => {
	     const faqItem = button.parentElement;
	     const answer = faqItem.querySelector(".faq-answer");

	     faqItem.classList.toggle("active");

	     if (faqItem.classList.contains("active")) {
	       answer.style.display = "block";
	     } else {
	       answer.style.display = "none";
	     }
	   });
	 });



