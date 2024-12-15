import React, { useRef } from 'react';
import { hide } from 'react-native-bootsplash';
import { FlatList, GestureHandlerRootView } from 'react-native-gesture-handler';
import { Provider } from 'react-redux';
import { StyleSheet } from 'react-native';
import { PersistGate } from 'redux-persist/integration/react';
import { persistor, store } from '@/store';
import { networkService } from '@/networking';
import { RootNavigator } from '@/navigation';
import { View ,Dimensions} from 'react-native';
import { requireNativeComponent } from 'react-native';
import ZoomableImageView from './components/ZoomableImageView';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export function App() {
  const flatListRef = useRef(null);

  const handleStoreRehydration = () => {
    const { accessToken } = store.getState().user;

    if (accessToken) {
      networkService.setAccessToken(accessToken);
    }

    hide();
  };
  const arrry = [1, 2, 3, 4, 5, 6, 7, 8, 910];
  return (
    <Provider store={store}>
      <PersistGate onBeforeLift={handleStoreRehydration} persistor={persistor}>
        <GestureHandlerRootView style={styles.container}>
          <View style={{ flex: 1, backgroundColor: 'red' }}>
            <FlatList
              ref={flatListRef}
              nestedScrollEnabled
              data={arrry}
              renderItem={() => {
                return (
                  <ZoomableImageView
                    style={{ width: Dimensions.get('window').width, height: '100%' }}
                    src="https://app-epimg.amarujala.com/2024/12/04/al/01/hdimage.jpg?s=fa98ace1780367e6e72671cfb64d122f"
                    maxScale={14} // Optional, default is 2.0
                    zoomEnabled={true}
                    onScaleChanged={event => {
                      console.log('----event----', event.nativeEvent);
                      const newScale = event.nativeEvent.newScale;
                      if (newScale < 0.71) {
                          flatListRef.current.setNativeProps({scrollEnabled: true});
                      } else {
                        flatListRef.current.setNativeProps({scrollEnabled: false});
                      }
                    }}
                  />
                );
              }}
              keyExtractor={(index)=> {
                return index
              }}
              horizontal={true}
              pagingEnabled
              contentContainerStyle={{flexGrow:1}}
              // scrollEnabled={false}
            />
          </View>
        </GestureHandlerRootView>
      </PersistGate>
    </Provider>
  );
}
