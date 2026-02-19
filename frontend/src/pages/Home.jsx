// import { Link } from 'react-router-dom';
// import { useState, useEffect } from 'react';
// import { Search, MapPin, Clock, Star, ArrowRight } from 'lucide-react';
// import { restaurantService } from '../services/api';
// import Loading from '../components/Loading';

// export default function Home() {
//   const [restaurants, setRestaurants] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [searchQuery, setSearchQuery] = useState('');

//   useEffect(() => {
//     loadRestaurants();
//   }, []);

//   const loadRestaurants = async () => {
//     try {
//       const response = await restaurantService.getAll();
//       setRestaurants(response.data.slice(0, 6)); // Show only 6 featured
//     } catch (error) {
//       console.error('Failed to load restaurants:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="min-h-screen">
//       {/* Hero Section */}
//       <section className="relative bg-gradient-to-r from-orange-500 to-orange-600 text-white">
//         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
//           <div className="max-w-3xl">
//             <h1 className="text-4xl md:text-6xl font-bold mb-6">
//               Delicious food,
//               <br />
//               delivered to you
//             </h1>
//             <p className="text-xl md:text-2xl mb-8 text-orange-100">
//               Order from your favorite restaurants and get it delivered right to your doorstep.
//             </p>
            
//             {/* Search Bar */}
//             <div className="bg-white rounded-lg shadow-lg p-2 flex items-center max-w-xl">
//               <div className="flex items-center px-3 text-gray-400">
//                 <MapPin className="h-5 w-5" />
//               </div>
//               <input
//                 type="text"
//                 placeholder="Enter your delivery address"
//                 className="flex-1 px-3 py-2 text-gray-700 focus:outline-none"
//                 value={searchQuery}
//                 onChange={(e) => setSearchQuery(e.target.value)}
//               />
//               <Link
//                 to="/restaurants"
//                 className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-lg font-semibold transition-colors"
//               >
//                 Find Food
//               </Link>
//             </div>
//           </div>
//         </div>
        
//         {/* Wave decoration */}
//         <div className="absolute bottom-0 left-0 right-0">
//           <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
//             <path
//               d="M0 120L60 105C120 90 240 60 360 45C480 30 600 30 720 37.5C840 45 960 60 1080 67.5C1200 75 1320 75 1380 75L1440 75V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z"
//               fill="#f9fafb"
//             />
//           </svg>
//         </div>
//       </section>

//       {/* Features Section */}
//       <section className="py-16 bg-gray-50">
//         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
//           <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
//             <div className="text-center p-6">
//               <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
//                 <Search className="h-8 w-8 text-orange-500" />
//               </div>
//               <h3 className="text-xl font-semibold mb-2">Easy to Order</h3>
//               <p className="text-gray-600">Browse restaurants and menus with just a few clicks</p>
//             </div>
//             <div className="text-center p-6">
//               <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
//                 <Clock className="h-8 w-8 text-orange-500" />
//               </div>
//               <h3 className="text-xl font-semibold mb-2">Fast Delivery</h3>
//               <p className="text-gray-600">Get your food delivered in minutes</p>
//             </div>
//             <div className="text-center p-6">
//               <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
//                 <Star className="h-8 w-8 text-orange-500" />
//               </div>
//               <h3 className="text-xl font-semibold mb-2">Best Quality</h3>
//               <p className="text-gray-600">Top-rated restaurants with quality food</p>
//             </div>
//           </div>
//         </div>
//       </section>

//       {/* Featured Restaurants */}
//       <section className="py-16">
//         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
//           <div className="flex justify-between items-center mb-8">
//             <h2 className="text-3xl font-bold text-gray-900">Featured Restaurants</h2>
//             <Link
//               to="/restaurants"
//               className="flex items-center text-orange-500 hover:text-orange-600 font-semibold"
//             >
//               View All <ArrowRight className="h-5 w-5 ml-1" />
//             </Link>
//           </div>

//           {loading ? (
//             <Loading message="Loading restaurants..." />
//           ) : restaurants.length === 0 ? (
//             <div className="text-center py-12 text-gray-500">
//               No restaurants available at the moment.
//             </div>
//           ) : (
//             <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
//               {restaurants.map((restaurant) => (
//                 <Link
//                   key={restaurant.id}
//                   to={`/restaurants/${restaurant.id}`}
//                   className="card overflow-hidden group"
//                 >
//                   <div className="h-48 bg-gradient-to-br from-orange-400 to-orange-600 flex items-center justify-center">
//                     <span className="text-6xl">🍽️</span>
//                   </div>
//                   <div className="p-4">
//                     <h3 className="text-xl font-semibold text-gray-900 group-hover:text-orange-500 transition-colors">
//                       {restaurant.name}
//                     </h3>
//                     <p className="text-gray-500 mt-1">{restaurant.address || 'Address not available'}</p>
//                     <div className="flex items-center mt-3 text-sm text-gray-600">
//                       <Star className="h-4 w-4 text-yellow-400 mr-1" />
//                       <span>4.5</span>
//                       <span className="mx-2">•</span>
//                       <Clock className="h-4 w-4 mr-1" />
//                       <span>25-35 min</span>
//                     </div>
//                   </div>
//                 </Link>
//               ))}
//             </div>
//           )}
//         </div>
//       </section>

//       {/* CTA Section */}
//       <section className="py-16 bg-orange-500">
//         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
//           <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
//             Ready to order?
//           </h2>
//           <p className="text-orange-100 text-lg mb-8">
//             Join thousands of happy customers ordering delicious food every day.
//           </p>
//           <div className="flex flex-col sm:flex-row gap-4 justify-center">
//             <Link
//               to="/signup"
//               className="inline-block bg-white text-orange-500 hover:bg-gray-100 px-8 py-3 rounded-lg font-semibold transition-colors"
//             >
//               Get Started
//             </Link>
//             <Link
//               to="/agent/login"
//               className="inline-block bg-orange-600 text-white hover:bg-orange-700 px-8 py-3 rounded-lg font-semibold transition-colors border-2 border-white"
//             >
//               Delivery Agent Portal →
//             </Link>
//           </div>
//         </div>
//       </section>
//     </div>
//   );
// }


import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { Search, MapPin, Clock, Star, ArrowRight, Sparkles, Zap, Shield } from 'lucide-react';
import { restaurantService } from '../services/api';
import Loading from '../components/Loading';

export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadRestaurants();
  }, []);

  const loadRestaurants = async () => {
    try {
      const response = await restaurantService.getAll();
      setRestaurants(response.data.slice(0, 6));
    } catch (error) {
      console.error('Failed to load restaurants:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen">
      {/* Enhanced Hero Section with Animated Gradient */}
      <section className="relative bg-gradient-to-br from-orange-500 via-orange-600 to-red-600 text-white overflow-hidden">
        {/* Animated Background Elements */}
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute -top-40 -right-40 w-80 h-80 bg-orange-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
          <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-red-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob" style={{ animationDelay: '2s' }}></div>
          <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-80 h-80 bg-yellow-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob" style={{ animationDelay: '4s' }}></div>
        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
          <div className="max-w-3xl">
            {/* Badge */}
            <div className="inline-flex items-center gap-2 bg-white/20 backdrop-blur-sm px-4 py-2 rounded-full mb-6 animate-fade-in-down">
              <Sparkles className="h-4 w-4" />
              <span className="text-sm font-medium">Fast & Fresh Delivery</span>
            </div>

            <h1 className="text-5xl md:text-7xl font-bold mb-6 animate-fade-in-up leading-tight">
              Delicious food,
              <br />
              <span className="bg-gradient-to-r from-yellow-200 to-orange-200 text-transparent bg-clip-text">
                delivered to you
              </span>
            </h1>
            <p className="text-xl md:text-2xl mb-10 text-orange-50 animate-fade-in-up leading-relaxed" style={{ animationDelay: '0.2s', animationFillMode: 'both' }}>
              Order from your favorite restaurants and get it delivered right to your doorstep in minutes.
            </p>
            
            {/* Enhanced Search Bar with Glassmorphism */}
            <div className="bg-white/95 backdrop-blur-md rounded-2xl shadow-2xl p-2 flex flex-col sm:flex-row items-stretch sm:items-center gap-2 max-w-2xl animate-fade-in-up border border-white/20" style={{ animationDelay: '0.4s', animationFillMode: 'both' }}>
              <div className="flex items-center flex-1 px-4 py-1">
                <MapPin className="h-5 w-5 text-orange-500 mr-3 flex-shrink-0" />
                <input
                  type="text"
                  placeholder="Enter your delivery address"
                  className="flex-1 py-2 text-gray-700 focus:outline-none placeholder-gray-400 bg-transparent"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
              <Link
                to="/restaurants"
                className="bg-gradient-to-r from-orange-500 to-orange-600 hover:from-orange-600 hover:to-orange-700 text-white px-8 py-3 rounded-xl font-semibold transition-all duration-300 transform hover:scale-105 hover:shadow-lg flex items-center justify-center gap-2"
              >
                <Search className="h-5 w-5" />
                Find Food
              </Link>
            </div>
          </div>
        </div>
        
        {/* Enhanced Wave Decoration */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full">
            <path
              d="M0 120L60 105C120 90 240 60 360 45C480 30 600 30 720 37.5C840 45 960 60 1080 67.5C1200 75 1320 75 1380 75L1440 75V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0Z"
              fill="#f9fafb"
            />
          </svg>
        </div>
      </section>

      {/* Enhanced Features Section with Cards */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">Why Choose Us</h2>
            <p className="text-gray-600 text-lg max-w-2xl mx-auto">
              Experience the best food delivery service with unmatched quality and speed
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 p-8 group border border-gray-100">
              <div className="w-16 h-16 bg-gradient-to-br from-orange-400 to-orange-600 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                <Search className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-xl font-bold mb-3 text-gray-900">Easy to Order</h3>
              <p className="text-gray-600 leading-relaxed">Browse hundreds of restaurants and thousands of menu items with our intuitive interface</p>
            </div>
            
            <div className="bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 p-8 group border border-gray-100">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-400 to-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                <Zap className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-xl font-bold mb-3 text-gray-900">Lightning Fast</h3>
              <p className="text-gray-600 leading-relaxed">Get your favorite meals delivered in 25-35 minutes or less, guaranteed fresh</p>
            </div>
            
            <div className="bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 p-8 group border border-gray-100">
              <div className="w-16 h-16 bg-gradient-to-br from-green-400 to-green-600 rounded-2xl flex items-center justify-center mx-auto mb-6 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                <Shield className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-xl font-bold mb-3 text-gray-900">Best Quality</h3>
              <p className="text-gray-600 leading-relaxed">Verified restaurants with top ratings and quality hygiene standards</p>
            </div>
          </div>
        </div>
      </section>

      {/* Enhanced Featured Restaurants */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-12 gap-4">
            <div>
              <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-2">Featured Restaurants</h2>
              <p className="text-gray-600">Discover the most popular restaurants in your area</p>
            </div>
            <Link
              to="/restaurants"
              className="flex items-center gap-2 text-orange-500 hover:text-orange-600 font-semibold group transition-colors"
            >
              View All 
              <ArrowRight className="h-5 w-5 group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>

          {loading ? (
            <Loading message="Loading restaurants..." />
          ) : restaurants.length === 0 ? (
            <div className="text-center py-20 bg-gray-50 rounded-2xl">
              <div className="text-6xl mb-4">🍽️</div>
              <p className="text-gray-500 text-lg">No restaurants available at the moment.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {restaurants.map((restaurant) => (
                <Link
                  key={restaurant.id}
                  to={`/restaurants/${restaurant.id}`}
                  className="group block"
                >
                  <div className="bg-white rounded-2xl shadow-md hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 overflow-hidden border border-gray-100">
                    <div className="relative h-56 bg-gradient-to-br from-orange-400 via-orange-500 to-orange-600 flex items-center justify-center overflow-hidden">
                      <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent"></div>
                      <span className="text-7xl relative z-10 group-hover:scale-110 transition-transform duration-300">🍽️</span>
                      
                      {/* Rating Badge */}
                      <div className="absolute top-4 right-4 bg-white/95 backdrop-blur-sm px-3 py-1.5 rounded-full flex items-center gap-1 shadow-lg">
                        <Star className="h-4 w-4 text-yellow-400 fill-yellow-400" />
                        <span className="font-semibold text-gray-900 text-sm">4.5</span>
                      </div>
                    </div>
                    
                    <div className="p-6">
                      <h3 className="text-xl font-bold text-gray-900 group-hover:text-orange-500 transition-colors mb-2 line-clamp-1">
                        {restaurant.name}
                      </h3>
                      <p className="text-gray-500 text-sm mb-4 flex items-start gap-2 line-clamp-2">
                        <MapPin className="h-4 w-4 mt-0.5 flex-shrink-0 text-gray-400" />
                        <span>{restaurant.address || 'Address not available'}</span>
                      </p>
                      
                      <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                        <div className="flex items-center gap-2 text-sm text-gray-600">
                          <Clock className="h-4 w-4 text-orange-500" />
                          <span className="font-medium">25-35 min</span>
                        </div>
                        <div className="text-orange-500 font-semibold text-sm flex items-center gap-1 group-hover:gap-2 transition-all">
                          Order Now
                          <ArrowRight className="h-4 w-4" />
                        </div>
                      </div>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Enhanced CTA Section with Gradient */}
      <section className="relative py-20 bg-gradient-to-br from-orange-500 via-orange-600 to-red-600 overflow-hidden">
        {/* Background Pattern */}
        <div className="absolute inset-0 opacity-10">
          <div className="absolute inset-0" style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
          }}></div>
        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="max-w-3xl mx-auto">
            <div className="inline-flex items-center gap-2 bg-white/20 backdrop-blur-sm px-4 py-2 rounded-full mb-6">
              <Sparkles className="h-4 w-4" />
              <span className="text-sm font-medium">Join Our Community</span>
            </div>
            
            <h2 className="text-4xl md:text-5xl font-bold text-white mb-6 leading-tight">
              Ready to order delicious food?
            </h2>
            <p className="text-orange-50 text-lg md:text-xl mb-10 leading-relaxed max-w-2xl mx-auto">
              Join thousands of happy customers ordering amazing food every day. Fast delivery, quality guaranteed.
            </p>
            
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/signup"
                className="inline-flex items-center justify-center gap-2 bg-white text-orange-600 hover:bg-gray-50 px-8 py-4 rounded-xl font-bold transition-all duration-300 transform hover:scale-105 shadow-xl hover:shadow-2xl"
              >
                <Sparkles className="h-5 w-5" />
                Get Started Free
              </Link>
              <Link
                to="/agent/login"
                className="inline-flex items-center justify-center gap-2 bg-orange-700/50 backdrop-blur-sm text-white hover:bg-orange-700/70 px-8 py-4 rounded-xl font-bold transition-all duration-300 border-2 border-white/30 hover:border-white/50 shadow-xl"
              >
                Delivery Agent Portal
                <ArrowRight className="h-5 w-5" />
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
