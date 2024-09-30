const toggleSidebar = () => {
    console.log(typeof $);

    let sidebar = $(".sidebar");
    let content = $(".content");

    if (sidebar.is(":visible")) {
        sidebar.hide(); // Hide sidebar
        content.css("margin-left", "1%"); // Adjust content margin
    } else {
        sidebar.show(); // Show sidebar
        content.css("margin-left", "20%"); // Adjust content margin to accommodate the sidebar
    }
};