document.addEventListener('DOMContentLoaded', () => {
    // Navbar scroll effect
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Mockup card active state cycle (visual effect for the hero section)
    const cards = document.querySelectorAll('.mockup-card');
    let currentIndex = 3; // Start with the 4th card (MemoryLeakFix) active

    setInterval(() => {
        // Remove active class from current
        cards[currentIndex].classList.remove('active-card');
        
        // Move to next
        currentIndex = (currentIndex + 1) % cards.length;
        
        // Add active class to new
        cards[currentIndex].classList.add('active-card');
    }, 2500);
});
